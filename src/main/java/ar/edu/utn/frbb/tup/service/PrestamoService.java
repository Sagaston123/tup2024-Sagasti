package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.model.Cliente;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.Prestamo;
import ar.edu.utn.frbb.tup.model.TipoMoneda;
import ar.edu.utn.frbb.tup.model.dto.PrestamoDto;
import ar.edu.utn.frbb.tup.model.dto.SolicitudPrestamoResponse;
import ar.edu.utn.frbb.tup.model.dto.PlanPagoDto;
import ar.edu.utn.frbb.tup.model.exception.PrestamoNotAllowedException;
import ar.edu.utn.frbb.tup.persistence.PrestamoDao;
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
    private CuentaService cuentaService;

    @Autowired
    private PrestamoDao prestamoDao;

    @Autowired
    private CreditScoringService creditScoringService;

    public SolicitudPrestamoResponse solicitarPrestamo(PrestamoDto prestamoDto) throws PrestamoNotAllowedException {
        Cliente cliente = clienteService.buscarClientePorDni(prestamoDto.getNumeroCliente());

        if (!creditScoringService.tieneBuenHistorial(cliente.getDni())) {
            throw new PrestamoNotAllowedException("El cliente no tiene una buena calificación crediticia.");
        }

        Cuenta cuentaDestino = cliente.getCuentas().stream()
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
        prestamo.setClienteId(cliente.getDni());
        prestamo.setMonto(monto);
        prestamo.setPlazoMeses(plazo);
        prestamo.setMoneda(TipoMoneda.valueOf(prestamoDto.getMoneda().toUpperCase()));
        prestamo.setFecha(LocalDate.now());
        prestamo.setPagosRealizados(0);
        prestamo.setSaldoRestante(monto);

        cuentaDestino.setBalance(cuentaDestino.getBalance() + monto);

        prestamoDao.save(prestamo);

        return new SolicitudPrestamoResponse("APROBADO",
                "El monto del préstamo fue acreditado en su cuenta",
                plan);
    }

    public List<Prestamo> obtenerPrestamosPorCliente(long clienteId) {
        return prestamoDao.findByClienteId(clienteId);
    }
}