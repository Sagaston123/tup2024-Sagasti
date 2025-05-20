package ar.edu.utn.frbb.tup.persistence;

import ar.edu.utn.frbb.tup.model.Cliente;
import ar.edu.utn.frbb.tup.model.dtos.ClienteDto;
import ar.edu.utn.frbb.tup.model.TipoPersona;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ClienteDaoTest {

    private ClienteDao clienteDao;

    @BeforeEach
    public void setup() {
        clienteDao = new ClienteDao();
        clienteDao.getInMemoryDatabase().clear(); 
    }

    @Test
    public void testSaveAndFindCliente() {
        ClienteDto dto = new ClienteDto();
        dto.setDni(1234L);
        dto.setNombre("Juan");
        dto.setApellido("Perez");
        dto.setFechaNacimiento("1990-01-01");
        dto.setTipoPersona(TipoPersona.PERSONA_FISICA);

        Cliente cliente = new Cliente(dto);
        clienteDao.save(cliente);

        Cliente resultado = clienteDao.find(1234L, false);

        assertNotNull(resultado);
        assertEquals(1234L, resultado.getDni());
    }

    @Test
    public void testFindClienteNoExiste() {
        Cliente resultado = clienteDao.find(9999L, false);
        assertNull(resultado);
    }

    @Test
    public void testGetAllClientes() {
        ClienteDto dto1 = new ClienteDto();
        dto1.setDni(1);
        dto1.setNombre("A");
        dto1.setApellido("X");
        dto1.setFechaNacimiento("1980-01-01");
        dto1.setTipoPersona(TipoPersona.PERSONA_FISICA);
        Cliente c1 = new Cliente(dto1);

        ClienteDto dto2 = new ClienteDto();
        dto2.setDni(2);
        dto2.setNombre("B");
        dto2.setApellido("Y");
        dto2.setFechaNacimiento("1990-01-01");
        dto2.setTipoPersona(TipoPersona.PERSONA_FISICA);
        Cliente c2 = new Cliente(dto2);

        clienteDao.save(c1);
        clienteDao.save(c2);

        List<Cliente> lista = clienteDao.getAll();
        assertEquals(2, lista.size());
    }
}