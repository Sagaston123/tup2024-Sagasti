package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.model.Cliente;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.Prestamo;
import ar.edu.utn.frbb.tup.model.TipoCuenta;
import ar.edu.utn.frbb.tup.model.TipoMoneda;
import ar.edu.utn.frbb.tup.model.dtos.PrestamoDto;
import ar.edu.utn.frbb.tup.model.dtos.SolicitudPrestamoResponse;
import ar.edu.utn.frbb.tup.model.exception.ClienteNotFoundException;
import ar.edu.utn.frbb.tup.model.exception.PrestamoNotAllowedException;
import ar.edu.utn.frbb.tup.persistence.PrestamoDao;
import ar.edu.utn.frbb.tup.persistence.CuentaDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PrestamoServiceTest {

    @Mock
    private ClienteService clienteService;

    @Mock
    private CuentaDao cuentaDao;

    @Mock
    private PrestamoDao prestamoDao;

    @Mock
    private CreditScoringService creditScoringService;

    @InjectMocks
    private PrestamoService prestamoService;

    private PrestamoDto buildDto() {
        PrestamoDto dto = new PrestamoDto();
        dto.setNumeroCliente(12345678L);
        dto.setMonto(100000);
        dto.setMoneda("PESOS");
        dto.setPlazoMeses(36);
        return dto;
    }

    private Cliente buildCliente() {
        Cliente cliente = new Cliente();
        cliente.setDni(12345678L);
        cliente.setBanco("Banco Falso");
        return cliente;
    }

    private Cuenta buildCuenta() {
        Cuenta cuenta = new Cuenta();
        cuenta.setDniTitular(12345678L);
        cuenta.setBalance(50000);
        cuenta.setTipoCuenta(TipoCuenta.CAJA_AHORRO);
        cuenta.setMoneda(TipoMoneda.PESOS);
        return cuenta;
    }

    @Test
    public void testPrestamoAprobado() throws Exception {
        PrestamoDto dto = buildDto();
        Cliente cliente = buildCliente();
        Cuenta cuenta = buildCuenta();

        when(clienteService.buscarClientePorDni(dto.getNumeroCliente())).thenReturn(cliente);
        when(creditScoringService.tieneBuenHistorial(cliente.getDni())).thenReturn(true);
        when(cuentaDao.getCuentasByCliente(cliente.getDni())).thenReturn(List.of(cuenta));

        SolicitudPrestamoResponse response = prestamoService.solicitarPrestamo(dto);

        assertEquals("APROBADO", response.getEstado());
        assertEquals(36, response.getPlanPagos().size());
        verify(prestamoDao, times(1)).save(any(Prestamo.class));
        verify(cuentaDao, times(1)).save(any(Cuenta.class));
    }

    @Test
    public void testClienteInexistente() {
        PrestamoDto dto = buildDto();

        when(clienteService.buscarClientePorDni(dto.getNumeroCliente())).thenThrow(new ClienteNotFoundException());

        assertThrows(ClienteNotFoundException.class, () -> prestamoService.solicitarPrestamo(dto));
    }

    @Test
    public void testClienteSinBuenHistorial() throws Exception {
        PrestamoDto dto = buildDto();
        Cliente cliente = buildCliente();

        when(clienteService.buscarClientePorDni(dto.getNumeroCliente())).thenReturn(cliente);
        when(creditScoringService.tieneBuenHistorial(cliente.getDni())).thenReturn(false);

        assertThrows(PrestamoNotAllowedException.class, () -> prestamoService.solicitarPrestamo(dto));
    }

    @Test
    public void testClienteSinCuentaEnMoneda() throws Exception {
        PrestamoDto dto = buildDto();
        Cliente cliente = buildCliente();

        when(clienteService.buscarClientePorDni(dto.getNumeroCliente())).thenReturn(cliente);
        when(creditScoringService.tieneBuenHistorial(cliente.getDni())).thenReturn(true);
        when(cuentaDao.getCuentasByCliente(cliente.getDni())).thenReturn(List.of());

        assertThrows(PrestamoNotAllowedException.class, () -> prestamoService.solicitarPrestamo(dto));
    }

    @Test
    public void testObtenerPrestamosPorCliente() throws Exception {
        Cliente cliente = buildCliente();
        Prestamo prestamo = new Prestamo();
        prestamo.setClienteId(cliente.getDni());
        prestamo.setMonto(50000);
        prestamo.setPlazoMeses(24);
        prestamo.setSaldoRestante(50000);
        prestamo.setPagosRealizados(0);

        when(clienteService.buscarClientePorDni(cliente.getDni())).thenReturn(cliente);
        when(prestamoDao.findByClienteId(cliente.getDni())).thenReturn(List.of(prestamo));

        List<Prestamo> resultado = prestamoService.obtenerPrestamosPorCliente(cliente.getDni());

        assertEquals(1, resultado.size());
        assertEquals(50000, resultado.get(0).getMonto());
    }

    @Test
    public void testObtenerPrestamosClienteInexistente() {
        when(clienteService.buscarClientePorDni(99999999L)).thenThrow(new ClienteNotFoundException());

        assertThrows(ClienteNotFoundException.class, () -> prestamoService.obtenerPrestamosPorCliente(99999999L));
    }
}
