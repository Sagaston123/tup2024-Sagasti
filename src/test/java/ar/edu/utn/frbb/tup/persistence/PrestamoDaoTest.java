package ar.edu.utn.frbb.tup.persistence;

import ar.edu.utn.frbb.tup.model.Prestamo;
import ar.edu.utn.frbb.tup.model.TipoMoneda;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PrestamoDaoTest {

    private PrestamoDao prestamoDao;

    @BeforeEach
    public void setup() {
        prestamoDao = new PrestamoDao();
        prestamoDao.getInMemoryDatabase().clear();
    }

    @Test
    public void testSaveAndFindPrestamoByClienteId() {
        Prestamo p = new Prestamo();
        p.setId(1L);
        p.setClienteId(777L);
        p.setMonto(50000);
        p.setPagosRealizados(0);
        p.setSaldoRestante(50000);
        p.setPlazoMeses(12);
        p.setFecha(LocalDate.now());
        p.setMoneda(TipoMoneda.PESOS);

        prestamoDao.save(p);

        List<Prestamo> lista = prestamoDao.findByClienteId(777L);
        assertEquals(1, lista.size());
        assertEquals(50000, lista.get(0).getMonto());
        assertEquals(777L, lista.get(0).getClienteId());
    }

    @Test
    public void testFindByClienteId_Empty() {
        List<Prestamo> lista = prestamoDao.findByClienteId(9999L);
        assertTrue(lista.isEmpty());
    }
}
