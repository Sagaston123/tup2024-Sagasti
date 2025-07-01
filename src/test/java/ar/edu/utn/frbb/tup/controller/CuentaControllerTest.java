package ar.edu.utn.frbb.tup.controller;

import ar.edu.utn.frbb.tup.controller.validator.CuentaValidator;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.dtos.CuentaDto;
import ar.edu.utn.frbb.tup.model.dtos.CuentaConTitularDto;
import ar.edu.utn.frbb.tup.model.dtos.ClienteDto;
import ar.edu.utn.frbb.tup.model.TipoPersona;
import ar.edu.utn.frbb.tup.model.exception.*;
import ar.edu.utn.frbb.tup.persistence.ClienteDao;
import ar.edu.utn.frbb.tup.persistence.CuentaDao;
import ar.edu.utn.frbb.tup.service.ClienteService;
import ar.edu.utn.frbb.tup.service.CuentaService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CuentaControllerTest {

    private CuentaController controller;
    private ClienteService clienteService;
    private CuentaService cuentaService;

    private void crearClienteSiNoExiste(long dni) throws ClienteAlreadyExistsException {
        try {
            clienteService.buscarClientePorDni(dni);
        } catch (ClienteNotFoundException e) {
            // Si lanza excepción, lo creamos
            ClienteDto clienteDto = new ClienteDto();
            clienteDto.setDni(dni);
            clienteDto.setNombre("Test");
            clienteDto.setApellido("User");
            clienteDto.setFechaNacimiento("1990-01-01");
            clienteDto.setBanco("FRBB");
            clienteDto.setTipoPersona(TipoPersona.PERSONA_FISICA);
            clienteService.darDeAltaCliente(clienteDto);
        }
    }

    @BeforeEach
    void setUp() throws ClienteAlreadyExistsException {
        ClienteDao clienteDao = new ClienteDao();
        CuentaDao cuentaDao = new CuentaDao();
        clienteService = new ClienteService(clienteDao, cuentaDao);
        cuentaService = new CuentaService(cuentaDao, clienteDao);

        controller = new CuentaController();
        ReflectionTestUtils.setField(controller, "cuentaService", cuentaService);
        ReflectionTestUtils.setField(controller, "cuentaValidator", new CuentaValidator());
    }

    @Test
    void shouldCreateAndListCuenta() throws Exception {
        long dni = 9999L;
        crearClienteSiNoExiste(dni);

        CuentaDto dto = new CuentaDto();
        dto.setClienteId(dni);
        dto.setSaldoInicial(1000);
        dto.setTipoCuenta("CAJA_AHORRO");
        dto.setMoneda("PESOS");

        Cuenta cuenta = controller.crearCuenta(dto);
        assertNotNull(cuenta);
        assertEquals(1000, cuenta.getBalance());

        List<Cuenta> cuentas = controller.listarCuentas();
        assertTrue(cuentas.size() >= 1); // puede haber más de una
    }

    @SuppressWarnings("deprecation")
    @Test
    void shouldConsultarCuenta() throws Exception {
        long dni = 2222L;
        crearClienteSiNoExiste(dni);

        CuentaDto dto = new CuentaDto();
        dto.setClienteId(dni);
        dto.setSaldoInicial(500);
        dto.setTipoCuenta("CUENTA_CORRIENTE");
        dto.setMoneda("PESOS");

        Cuenta cuenta = controller.crearCuenta(dto);

        ResponseEntity<CuentaConTitularDto> response = controller.consultarCuenta(cuenta.getNumeroCuenta());
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(dni, response.getBody().getTitular().getDni());
    }

    @SuppressWarnings("deprecation")
    @Test
    void shouldDepositarEnCuenta() throws Exception {
        long dni = 4321L;
        crearClienteSiNoExiste(dni);

        CuentaDto dto = new CuentaDto();
        dto.setClienteId(dni);
        dto.setSaldoInicial(200);
        dto.setTipoCuenta("CUENTA_CORRIENTE");
        dto.setMoneda("PESOS");

        Cuenta cuenta = controller.crearCuenta(dto);

        ResponseEntity<String> response = controller.depositar(cuenta.getNumeroCuenta(), 300);
        assertEquals(200, response.getStatusCodeValue());

        Cuenta actualizada = cuentaService.consultarCuenta(cuenta.getNumeroCuenta());
        assertEquals(500, actualizada.getBalance());
    }

    @SuppressWarnings("deprecation")
    @Test
    void shouldExtraerDeCuenta() throws Exception {
        long dni = 3332L;
        crearClienteSiNoExiste(dni);

        CuentaDto dto = new CuentaDto();
        dto.setClienteId(dni);
        dto.setSaldoInicial(500);
        dto.setTipoCuenta("CUENTA_CORRIENTE");
        dto.setMoneda("PESOS");

        Cuenta cuenta = controller.crearCuenta(dto);

        ResponseEntity<String> response = controller.extraer(cuenta.getNumeroCuenta(), 200);
        assertEquals(200, response.getStatusCodeValue());

        Cuenta actualizada = cuentaService.consultarCuenta(cuenta.getNumeroCuenta());
        assertEquals(300, actualizada.getBalance());
    }

    @SuppressWarnings("deprecation")
    @Test
    void shouldTransferirEntreCuentas() throws Exception {
        long dniOrigen = 7722L;
        long dniDestino = 7723L;

        crearClienteSiNoExiste(dniOrigen);
        crearClienteSiNoExiste(dniDestino);

        // Cuenta origen
        CuentaDto dto1 = new CuentaDto();
        dto1.setClienteId(dniOrigen);
        dto1.setSaldoInicial(1000);
        dto1.setTipoCuenta("CAJA_AHORRO");
        dto1.setMoneda("PESOS");
        Cuenta origen = controller.crearCuenta(dto1);

        // Cuenta destino
        CuentaDto dto2 = new CuentaDto();
        dto2.setClienteId(dniDestino);
        dto2.setSaldoInicial(0);
        dto2.setTipoCuenta("CAJA_AHORRO"); // mismo tipo
        dto2.setMoneda("PESOS");           // misma moneda
        Cuenta destino = controller.crearCuenta(dto2);

        ResponseEntity<String> response = controller.transferir(origen.getNumeroCuenta(), destino.getNumeroCuenta(), 400);
        assertEquals(200, response.getStatusCodeValue(), "La transferencia debe ser exitosa (200 OK)");

        assertEquals(600, cuentaService.consultarCuenta(origen.getNumeroCuenta()).getBalance());
        assertEquals(400, cuentaService.consultarCuenta(destino.getNumeroCuenta()).getBalance());
    }

}
