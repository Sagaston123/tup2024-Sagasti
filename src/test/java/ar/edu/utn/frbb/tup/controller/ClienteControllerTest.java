package ar.edu.utn.frbb.tup.controller;

import ar.edu.utn.frbb.tup.controller.validator.ClienteValidator;
import ar.edu.utn.frbb.tup.model.Cliente;
import ar.edu.utn.frbb.tup.model.TipoPersona;
import ar.edu.utn.frbb.tup.model.dtos.ClienteDto;
import ar.edu.utn.frbb.tup.model.exception.ClienteAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.ClienteNotFoundException;
import ar.edu.utn.frbb.tup.persistence.ClienteDao;
import ar.edu.utn.frbb.tup.persistence.CuentaDao;
import ar.edu.utn.frbb.tup.service.ClienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ClienteControllerTest {

    private ClienteController controller;
    
    @BeforeEach
    void setUp() {
        ClienteDao clienteDao = new ClienteDao();
        CuentaDao cuentaDao = new CuentaDao();

        ClienteService service = new ClienteService(clienteDao, cuentaDao);
        ClienteValidator validator = new ClienteValidator();

        controller = new ClienteController();
        ReflectionTestUtils.setField(controller, "clienteService", service);
        ReflectionTestUtils.setField(controller, "clienteValidator", validator);
    }

    @Test
    void shouldCreateAndFetchClienteByDni() throws ClienteAlreadyExistsException, ClienteNotFoundException {
        ClienteDto dto = new ClienteDto();
        dto.setDni(1234L);
        dto.setNombre("Juan");
        dto.setApellido("Perez");
        dto.setFechaNacimiento("1990-01-01");
        dto.setBanco("FRBB");
        dto.setTipoPersona(TipoPersona.PERSONA_FISICA);

        Cliente created = controller.crearCliente(dto);
        assertNotNull(created);
        assertEquals("Juan", created.getNombre());

        ResponseEntity<Cliente> response = controller.buscarClientePorDni(1234L);
        Cliente found = response.getBody();

        assertNotNull(found);
        assertEquals("Perez", found.getApellido());
    }

    @Test
    void shouldListAllClientes() throws ClienteAlreadyExistsException {
        ClienteDto dto = new ClienteDto();
        dto.setDni(5678L);
        dto.setNombre("Ana");
        dto.setApellido("Gomez");
        dto.setFechaNacimiento("1985-06-15");
        dto.setBanco("FRBB");
        dto.setTipoPersona(TipoPersona.PERSONA_JURIDICA);

        controller.crearCliente(dto);

        List<Cliente> list = controller.listarClientes();
        assertEquals(1, list.size());
        assertEquals("Ana", list.get(0).getNombre());
    }
}
