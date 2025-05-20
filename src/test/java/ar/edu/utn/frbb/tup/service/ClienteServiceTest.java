package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.model.Cliente;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.TipoCuenta;
import ar.edu.utn.frbb.tup.model.TipoMoneda;
import ar.edu.utn.frbb.tup.model.TipoPersona;
import ar.edu.utn.frbb.tup.model.dtos.ClienteDto;
import ar.edu.utn.frbb.tup.model.exception.ClienteAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.ClienteNotFoundException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.persistence.ClienteDao;
import ar.edu.utn.frbb.tup.persistence.CuentaDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClienteServiceTest {

    @Mock
    private ClienteDao clienteDao;

    @Mock
    private CuentaDao cuentaDao;

    @InjectMocks
    private ClienteService clienteService;

    private ClienteDto buildDto(long dni, String nacimiento) {
        ClienteDto dto = new ClienteDto();
        dto.setDni(dni);
        dto.setNombre("Juan");
        dto.setApellido("Pérez");
        dto.setFechaNacimiento(nacimiento);
        dto.setBanco("Banco Falso");
        dto.setTipoPersona(TipoPersona.PERSONA_FISICA);
        return dto;
    }

    @Test
    public void testDarDeAltaCliente_OK() throws Exception {
        ClienteDto dto = buildDto(12345678L, "1990-01-01");
        when(clienteDao.find(dto.getDni(), false)).thenReturn(null);

        Cliente cliente = clienteService.darDeAltaCliente(dto);

        assertEquals(dto.getDni(), cliente.getDni());
        verify(clienteDao).save(any(Cliente.class));
    }

    @Test
    public void testDarDeAltaCliente_YaExiste() {
        ClienteDto dto = buildDto(12345678L, "1990-01-01");
        Cliente existente = new Cliente();
        existente.setDni(12345678L);
        when(clienteDao.find(dto.getDni(), false)).thenReturn(existente);

        assertThrows(ClienteAlreadyExistsException.class, () -> clienteService.darDeAltaCliente(dto));
    }

    @Test
    public void testDarDeAltaCliente_MenorDeEdad() {
        ClienteDto dto = buildDto(12345678L, LocalDate.now().minusYears(10).toString());
        when(clienteDao.find(dto.getDni(), false)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> clienteService.darDeAltaCliente(dto));
    }

    @Test
    public void testValidarCuentaNueva_OK() throws Exception {
        long dni = 12345678L;
        Cuenta nueva = new Cuenta()
                .setTipoCuenta(TipoCuenta.CAJA_AHORRO)
                .setMoneda(TipoMoneda.PESOS)
                .setBalance(1000);
        nueva.setDniTitular(dni);

        when(clienteDao.find(dni, false)).thenReturn(new Cliente());
        when(cuentaDao.getAll()).thenReturn(List.of()); // sin cuentas duplicadas

        clienteService.validarCuentaNueva(nueva, dni);
    }

    @Test
    public void testValidarCuentaNueva_Duplicada() {
        long dni = 12345678L;

        Cuenta existente = new Cuenta()
                .setTipoCuenta(TipoCuenta.CAJA_AHORRO)
                .setMoneda(TipoMoneda.PESOS);
        existente.setDniTitular(dni);

        Cuenta nueva = new Cuenta()
                .setTipoCuenta(TipoCuenta.CAJA_AHORRO)
                .setMoneda(TipoMoneda.PESOS);
        nueva.setDniTitular(dni);

        when(clienteDao.find(dni, false)).thenReturn(new Cliente());
        when(cuentaDao.getAll()).thenReturn(List.of(existente));

        assertThrows(TipoCuentaAlreadyExistsException.class, () -> clienteService.validarCuentaNueva(nueva, dni));
    }

    @Test
    public void testValidarCuentaNueva_ClienteNoExiste() {
        long dni = 12345678L;
        Cuenta cuenta = new Cuenta().setTipoCuenta(TipoCuenta.CAJA_AHORRO).setMoneda(TipoMoneda.PESOS);
        cuenta.setDniTitular(dni);

        when(clienteDao.find(dni, false)).thenReturn(null);

        assertThrows(ClienteNotFoundException.class, () -> clienteService.validarCuentaNueva(cuenta, dni));
    }

    @Test
    public void testBuscarClientePorDni_OK() throws Exception {
        Cliente esperado = new Cliente();
        esperado.setDni(12345678L);

        when(clienteDao.find(12345678L, true)).thenReturn(esperado);

        Cliente resultado = clienteService.buscarClientePorDni(12345678L);

        assertEquals(12345678L, resultado.getDni());
    }

    @Test
    public void testBuscarClientePorDni_NoExiste() {
        when(clienteDao.find(12345678L, true)).thenReturn(null);

        assertThrows(ClienteNotFoundException.class, () -> clienteService.buscarClientePorDni(12345678L));
    }

    @Test
    public void testListarTodosClientes_OK() {
        Cliente c1 = new Cliente(); c1.setDni(1L);
        Cliente c2 = new Cliente(); c2.setDni(2L);
        when(clienteDao.getAll()).thenReturn(List.of(c1, c2));

        List<Cliente> lista = clienteService.listarTodos();

        assertEquals(2, lista.size());
    }

    @Test
    public void testListarTodosClientes_Vacio() {
        when(clienteDao.getAll()).thenReturn(List.of());

        List<Cliente> lista = clienteService.listarTodos();

        assertTrue(lista.isEmpty());
    }
}