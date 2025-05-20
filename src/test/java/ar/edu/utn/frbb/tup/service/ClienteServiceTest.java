package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.model.dtos.ClienteDto;
import ar.edu.utn.frbb.tup.model.Cliente;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.TipoCuenta;
import ar.edu.utn.frbb.tup.model.TipoMoneda;
import ar.edu.utn.frbb.tup.model.TipoPersona;
import ar.edu.utn.frbb.tup.model.exception.ClienteAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.ClienteNotFoundException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.persistence.ClienteDao;
import ar.edu.utn.frbb.tup.persistence.CuentaDao;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ClienteServiceTest {

    @Mock
    private ClienteDao clienteDao;

    @Mock
    private CuentaDao cuentaDao;

    @InjectMocks
    private ClienteService clienteService;

    @BeforeAll
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testClienteMenor18Años() {
        ClienteDto clienteMenorDeEdad = new ClienteDto();
        clienteMenorDeEdad.setFechaNacimiento("2020-03-18");
        assertThrows(IllegalArgumentException.class, () -> clienteService.darDeAltaCliente(clienteMenorDeEdad));
    }

    @Test
    public void testClienteSuccess() throws ClienteAlreadyExistsException {
        ClienteDto cliente = new ClienteDto();
        cliente.setFechaNacimiento("1978-03-18");
        cliente.setDni(29857643);

        when(clienteDao.find(29857643, false)).thenReturn(null);

        Cliente clienteEntity = clienteService.darDeAltaCliente(cliente);

        verify(clienteDao, times(1)).save(clienteEntity);
    }

    @Test
    public void testClienteAlreadyExistsException() throws ClienteAlreadyExistsException {
        ClienteDto pepeRino = new ClienteDto();
        pepeRino.setDni(26456437);
        pepeRino.setNombre("Pepe");
        pepeRino.setApellido("Rino");
        pepeRino.setFechaNacimiento("1978-03-18");

        when(clienteDao.find(26456437, false)).thenReturn(new Cliente());

        assertThrows(ClienteAlreadyExistsException.class, () -> clienteService.darDeAltaCliente(pepeRino));
    }

    @Test
    public void testValidarCuentaNuevaSuccess() throws TipoCuentaAlreadyExistsException, ClienteNotFoundException {
        Cliente pepeRino = new Cliente();
        pepeRino.setDni(26456439);
        pepeRino.setNombre("Pepe");
        pepeRino.setApellido("Rino");
        pepeRino.setFechaNacimiento(LocalDate.of(1978, 3,25));
        pepeRino.setTipoPersona(TipoPersona.PERSONA_FISICA);

        Cuenta cuenta = new Cuenta()
                .setMoneda(TipoMoneda.PESOS)
                .setBalance(500000)
                .setTipoCuenta(TipoCuenta.CAJA_AHORRO);

        when(clienteDao.find(26456439, false)).thenReturn(pepeRino);
        when(cuentaDao.getAll()).thenReturn(new ArrayList<>());

        // No debe lanzar excepción
        clienteService.validarCuentaNueva(cuenta, pepeRino.getDni());
    }

    @Test
    public void testValidarCuentaNuevaDuplicada() throws TipoCuentaAlreadyExistsException, ClienteNotFoundException {
        Cliente luciano = new Cliente();
        luciano.setDni(26456439);
        luciano.setNombre("Luciano");
        luciano.setApellido("Perez");
        luciano.setFechaNacimiento(LocalDate.of(1980, 1, 1));
        luciano.setTipoPersona(TipoPersona.PERSONA_FISICA);

        Cuenta cuentaExistente = new Cuenta()
                .setMoneda(TipoMoneda.PESOS)
                .setBalance(1000)
                .setTipoCuenta(TipoCuenta.CAJA_AHORRO);
        cuentaExistente.setDniTitular(26456439);

        when(clienteDao.find(26456439, false)).thenReturn(luciano);
        when(cuentaDao.getAll()).thenReturn(List.of(cuentaExistente));

        Cuenta cuentaDuplicada = new Cuenta()
                .setMoneda(TipoMoneda.PESOS)
                .setBalance(500000)
                .setTipoCuenta(TipoCuenta.CAJA_AHORRO);

        assertThrows(TipoCuentaAlreadyExistsException.class, () -> clienteService.validarCuentaNueva(cuentaDuplicada, luciano.getDni()));
    }

    @Test
    public void testValidarCuentaNuevaClienteNoExiste() {
        Cuenta cuenta = new Cuenta()
                .setMoneda(TipoMoneda.PESOS)
                .setBalance(500000)
                .setTipoCuenta(TipoCuenta.CAJA_AHORRO);

        when(clienteDao.find(12345678, false)).thenReturn(null);

        assertThrows(ClienteNotFoundException.class, () -> clienteService.validarCuentaNueva(cuenta, 12345678));
    }
}