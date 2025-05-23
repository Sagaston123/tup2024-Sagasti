package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.model.Cliente;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.Prestamo;
import ar.edu.utn.frbb.tup.model.TipoMoneda;
import ar.edu.utn.frbb.tup.model.dtos.PrestamoDto;
import ar.edu.utn.frbb.tup.model.dtos.SolicitudPrestamoResponse;
import ar.edu.utn.frbb.tup.model.dtos.PlanPagoDto;
import ar.edu.utn.frbb.tup.model.exception.ClienteNotFoundException;
import ar.edu.utn.frbb.tup.model.exception.PrestamoNotAllowedException;
import ar.edu.utn.frbb.tup.persistence.PrestamoDao;
import ar.edu.utn.frbb.tup.persistence.PrestamoIdGenerator;
import ar.edu.utn.frbb.tup.persistence.CuentaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PrestamoService {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private PrestamoDao prestamoDao;

    @Autowired
    private CreditScoringService creditScoringService;

    @Autowired
    private CuentaDao cuentaDao;

    public SolicitudPrestamoResponse solicitarPrestamo(PrestamoDto prestamoDto) throws PrestamoNotAllowedException, ClienteNotFoundException {
        Cliente cliente = clienteService.buscarClientePorDni(prestamoDto.getNumeroCliente());

        if (!creditScoringService.tieneBuenHistorial(cliente.getDni())) {
            throw new PrestamoNotAllowedException("El cliente no tiene una buena calificación crediticia.");
        }

        List<Cuenta> cuentasDelCliente = cuentaDao.getCuentasByCliente(cliente.getDni());

        Cuenta cuentaDestino = cuentasDelCliente.stream()
                .filter(c -> c.getMoneda().toString().equalsIgnoreCase(prestamoDto.getMoneda()))
                .findFirst()
                .orElseThrow(() -> new PrestamoNotAllowedException("El cliente no tiene cuenta en la moneda solicitada."));

        double interesAnual = 0.05;
        double tasaMensual = interesAnual / 12;
        int plazo = prestamoDto.getPlazoMeses();
        double monto = prestamoDto.getMonto();
        double cuotaMensual = (monto * tasaMensual) / (1 - Math.pow(1 + tasaMensual, -plazo));

        List<PlanPagoDto> plan = new ArrayList<>();
        for (int i = 1; i <= plazo; i++) {
            plan.add(new PlanPagoDto(i, cuotaMensual));
        }

        Prestamo prestamo = new Prestamo();
        prestamo.setId(PrestamoIdGenerator.generarNuevoId());
        prestamo.setClienteId(cliente.getDni());
        prestamo.setMonto(monto);
        prestamo.setPlazoMeses(plazo);
        prestamo.setMoneda(TipoMoneda.valueOf(prestamoDto.getMoneda().toUpperCase()));
        prestamo.setFecha(LocalDate.now());
        prestamo.setPagosRealizados(0);
        prestamo.setSaldoRestante(monto);

        cuentaDestino.setBalance(cuentaDestino.getBalance() + monto);
        cuentaDao.save(cuentaDestino);
        prestamoDao.save(prestamo);

        return new SolicitudPrestamoResponse("APROBADO",
                "El monto del préstamo fue acreditado en su cuenta",
                plan);
    }

    public List<Prestamo> obtenerPrestamosPorCliente(long clienteId) {
        clienteService.buscarClientePorDni(clienteId); 
        return prestamoDao.findByClienteId(clienteId);
    }
}