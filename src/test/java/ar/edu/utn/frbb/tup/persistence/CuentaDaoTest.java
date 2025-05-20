package ar.edu.utn.frbb.tup.persistence;

import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.TipoCuenta;
import ar.edu.utn.frbb.tup.model.TipoMoneda;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CuentaDaoTest {

    private CuentaDao cuentaDao;

    @BeforeEach
    public void setup() {
        cuentaDao = new CuentaDao();
        cuentaDao.getInMemoryDatabase().clear();
    }

    @Test
    public void testSaveAndFindCuenta() {
        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(1L);
        cuenta.setDniTitular(1234L);
        cuenta.setBalance(1000);
        cuenta.setTipoCuenta(TipoCuenta.CAJA_AHORRO);
        cuenta.setMoneda(TipoMoneda.PESOS);
        cuenta.setFechaCreacion(LocalDateTime.now());

        cuentaDao.save(cuenta);
        Cuenta resultado = cuentaDao.find(1L);

        assertNotNull(resultado);
        assertEquals(1234L, resultado.getDniTitular());
    }

    @Test
    public void testGetCuentasByCliente() {
        Cuenta c1 = new Cuenta();
        c1.setNumeroCuenta(1L);
        c1.setDniTitular(123L);
        c1.setTipoCuenta(TipoCuenta.CAJA_AHORRO);
        c1.setMoneda(TipoMoneda.PESOS);
        c1.setFechaCreacion(LocalDateTime.now());

        Cuenta c2 = new Cuenta();
        c2.setNumeroCuenta(2L);
        c2.setDniTitular(456L);
        c2.setTipoCuenta(TipoCuenta.CAJA_AHORRO);
        c2.setMoneda(TipoMoneda.PESOS);
        c2.setFechaCreacion(LocalDateTime.now());

        cuentaDao.save(c1);
        cuentaDao.save(c2);

        List<Cuenta> cuentas = cuentaDao.getCuentasByCliente(123L);
        assertEquals(1, cuentas.size());
        assertEquals(123L, cuentas.get(0).getDniTitular());
    }
}
