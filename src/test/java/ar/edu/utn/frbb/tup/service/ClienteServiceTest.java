package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.model.*;
import ar.edu.utn.frbb.tup.model.exception.ClienteAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.persistence.ClienteDao;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ClienteServiceTest {

    @Mock
    private ClienteDao clienteDao;

    @InjectMocks
    private ClienteService clienteService;

    @BeforeAll
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testClienteMenor18Años() {
        Cliente clienteMenorDeEdad = new Cliente();
        clienteMenorDeEdad.setFechaNacimiento(LocalDate.of(2020, 2, 7));
        assertThrows(IllegalArgumentException.class, () -> clienteService.darDeAltaCliente(clienteMenorDeEdad));
    }

    @Test
    public void testClienteSuccess() throws ClienteAlreadyExistsException {
        Cliente cliente = new Cliente();
        cliente.setFechaNacimiento(LocalDate.of(1978,3,25));
        cliente.setDni(29857643);
        cliente.setTipoPersona(TipoPersona.PERSONA_FISICA);
        clienteService.darDeAltaCliente(cliente);

        verify(clienteDao, times(1)).save(cliente);
    }

    @Test
    public void testClienteAlreadyExistsException() throws ClienteAlreadyExistsException {
        Cliente pepeRino = new Cliente();
        pepeRino.setDni(26456437);
        pepeRino.setNombre("Pepe");
        pepeRino.setApellido("Rino");
        pepeRino.setFechaNacimiento(LocalDate.of(1978, 3,25));
        pepeRino.setTipoPersona(TipoPersona.PERSONA_FISICA);

        when(clienteDao.find(26456437, false)).thenReturn(new Cliente()); //Preparamos el mock


        assertThrows(ClienteAlreadyExistsException.class, () -> clienteService.darDeAltaCliente(pepeRino)); //Lanza una exepction
    }

    //Completar este test unitario
    @Test
    public void testAgregarCuentaAClienteSuccess() throws TipoCuentaAlreadyExistsException {
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

        when(clienteDao.find(26456439, true)).thenReturn(pepeRino); //Preparamos al mock para el caso feliz  

        clienteService.agregarCuenta(cuenta, pepeRino.getDni());

        assertEquals(1, pepeRino.getCuentas().size()); //Verifico que peperino tenga una sola cuenta
        assertEquals(pepeRino, cuenta.getTitular());    //Verifico que peperino sea el diuenio de la cuenta
    }

    //Agregar una CA$ y agregar otra cuenta con mismo tipo y moneda --> fallar (assertThrows)
    @Test
    public void testCliente2CuentasIguales() throws TipoCuentaAlreadyExistsException {
        Cliente gaston = new Cliente();
        gaston.setDni(48357988);
        gaston.setNombre("Gaston");
        gaston.setApellido("Sagasti");
        gaston.setFechaNacimiento(LocalDate.of(2002,4,19));
        gaston.setTipoPersona(TipoPersona.PERSONA_FISICA);
        
        Cuenta cuenta = new Cuenta()
                .setMoneda(TipoMoneda.PESOS)
                .setBalance(500000)
                .setTipoCuenta(TipoCuenta.CAJA_AHORRO);
        
        when(clienteDao.find(48357988, true)).thenReturn(gaston);
        clienteService.agregarCuenta(cuenta, gaston.getDni());

        Cuenta cuenta2 = new Cuenta()
                .setMoneda(TipoMoneda.PESOS)
                .setBalance(500000)
                .setTipoCuenta(TipoCuenta.CAJA_AHORRO);

        
        assertThrows(TipoCuentaAlreadyExistsException.class, () -> clienteService.agregarCuenta(cuenta2, gaston.getDni()));

        assertEquals(1, gaston.getCuentas().size()); //Verifico que peperino tenga una sola cuenta
        assertEquals(gaston, cuenta.getTitular());    //Verifico que peperino sea el diuenio de la cuenta
    }
    //Agregar una CA$ y CC$ --> success 2 cuentas, titular peperino
    @Test
    public void testCuentaAhorroyCorrienteSuccess() throws TipoCuentaAlreadyExistsException {
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

        Cuenta cuenta2 = new Cuenta()
                .setMoneda(TipoMoneda.PESOS)
                .setBalance(2000)
                .setTipoCuenta(TipoCuenta.CUENTA_CORRIENTE);

        when(clienteDao.find(26456439, true)).thenReturn(pepeRino); //Preparamos al mock para el caso feliz 

        clienteService.agregarCuenta(cuenta, pepeRino.getDni());
        clienteService.agregarCuenta(cuenta2, pepeRino.getDni());

        assertEquals(2, pepeRino.getCuentas().size()); //Verifico que peperino tenga 2 cuentas
        assertEquals(pepeRino, cuenta.getTitular());    //Verifico que peperino sea el dueno de la primera cuenta
        assertEquals(pepeRino, cuenta2.getTitular()); 
       // assertTrue(pepeRino.getCuentas().contains(cuenta));  //Verifico que la primera cuenta este en la list de cuentas del titular
       // assertTrue(pepeRino.getCuentas().contains(cuenta2));  
    }

    //Agregar una CA$ y CAU$S --> success 2 cuentas, titular peperino...
    @Test
    public void testAgregarCAyCAUS() throws TipoCuentaAlreadyExistsException {
        Cliente pepeRino = new Cliente();
        pepeRino.setDni(26456439);
        pepeRino.setNombre("Pepe");
        pepeRino.setApellido("Rino");
        pepeRino.setFechaNacimiento(LocalDate.of(1978, 3, 25));
        pepeRino.setTipoPersona(TipoPersona.PERSONA_FISICA);

        Cuenta cuentaCA = new Cuenta();
        cuentaCA.setMoneda(TipoMoneda.PESOS);
        cuentaCA.setBalance(500000);
        cuentaCA.setTipoCuenta(TipoCuenta.CAJA_AHORRO);

        Cuenta cuentaCAUS = new Cuenta();
        cuentaCAUS.setMoneda(TipoMoneda.DOLARES);
        cuentaCAUS.setBalance(100000);
        cuentaCAUS.setTipoCuenta(TipoCuenta.CAJA_AHORRO);

        when(clienteDao.find(26456439, true)).thenReturn(pepeRino);

        clienteService.agregarCuenta(cuentaCA, pepeRino.getDni());
        clienteService.agregarCuenta(cuentaCAUS, pepeRino.getDni());

        assertEquals(2, pepeRino.getCuentas().size());
        assertEquals(pepeRino, cuentaCA.getTitular());
        assertEquals(pepeRino, cuentaCAUS.getTitular());

        verify(clienteDao, times(2)).save(pepeRino);
    }

    //Testear clienteService.buscarPorDni
    @Test
    public void testClienteServiceBuscarPorDniExito() throws ClienteAlreadyExistsException {
           Cliente pepeRino = new Cliente();
           pepeRino.setDni(46339672);
           pepeRino.setNombre("Pepe");
           pepeRino.setApellido("Rino");
           pepeRino.setFechaNacimiento(LocalDate.of(2005, 3, 2));
           pepeRino.setTipoPersona(TipoPersona.PERSONA_FISICA);

           when(clienteDao.find(46339672, true)).thenReturn(pepeRino);

           Cliente resultadoBuscarDNI = clienteService.buscarClientePorDni(46339672);

           assertNotNull(resultadoBuscarDNI); // Verificamos que el resultadoBuscarDNI no sea null
           assertEquals(pepeRino.getDni(), resultadoBuscarDNI.getDni()); //verificar que el DNI del cliente devuelto coincide con el DNI del cliente configurado

           verify(clienteDao, times(1)).find(46339672, true);
    } 
}