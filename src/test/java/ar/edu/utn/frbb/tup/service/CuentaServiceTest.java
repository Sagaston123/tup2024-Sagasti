package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.model.Cliente;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.TipoCuenta;
import ar.edu.utn.frbb.tup.model.TipoMoneda;
import ar.edu.utn.frbb.tup.model.dtos.CuentaDto;
import ar.edu.utn.frbb.tup.model.exception.*;
import ar.edu.utn.frbb.tup.persistence.ClienteDao;
import ar.edu.utn.frbb.tup.persistence.CuentaDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
public class CuentaServiceTest {

    @Mock
    private CuentaDao cuentaDao;

    @Mock
    private ClienteDao clienteDao;

    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private CuentaService cuentaService;

    private CuentaDto buildDto() {
        CuentaDto dto = new CuentaDto();
        dto.setTipoCuenta("CAJA_AHORRO");
        dto.setMoneda("PESOS");
        dto.setSaldoInicial(1000);
        return dto;
    }

    private Cuenta buildCuenta(long numero) {
        Cuenta c = new Cuenta();
        c.setNumeroCuenta(numero);
        c.setTipoCuenta(TipoCuenta.CAJA_AHORRO);
        c.setMoneda(TipoMoneda.PESOS);
        c.setBalance(1000);
        return c;
    }

    @Test
    public void testCrearCuenta_OK() throws Exception {
        CuentaDto dto = buildDto();
        Cliente cliente = new Cliente();
        cliente.setDni(123L);

        when(clienteDao.find(123L, false)).thenReturn(cliente);

        Cuenta cuenta = cuentaService.crearCuenta(dto, 123L);

        assertEquals(123L, cuenta.getDniTitular());
        verify(cuentaDao).save(any(Cuenta.class));
    }

    @Test
    public void testCrearCuenta_ClienteInexistente() {
        when(clienteDao.find(123L, false)).thenReturn(null);
        assertThrows(ClienteNotFoundException.class, () -> cuentaService.crearCuenta(buildDto(), 123L));
    }

    @Test
    public void testCrearCuenta_TipoInvalido() {
        CuentaDto dto = new CuentaDto();
        dto.setTipoCuenta("INVALIDO");
        dto.setMoneda("PESOS");
        dto.setSaldoInicial(1000);
        when(clienteDao.find(123L, false)).thenReturn(new Cliente());

        assertThrows(TipoCuentaNotSupportedException.class, () -> cuentaService.crearCuenta(dto, 123L));
    }

    @Test
    public void testConsultarCuenta_OK() throws Exception {
        Cuenta cuenta = buildCuenta(1L);
        when(cuentaDao.find(1L)).thenReturn(cuenta);

        Cuenta resultado = cuentaService.consultarCuenta(1L);

        assertEquals(1L, resultado.getNumeroCuenta());
    }

    @Test
    public void testConsultarCuenta_NoExiste() {
        when(cuentaDao.find(99L)).thenReturn(null);

        assertThrows(CuentaNotFoundException.class, () -> cuentaService.consultarCuenta(99L));
    }

    @Test
    public void testDepositar_OK() throws Exception {
        Cuenta cuenta = buildCuenta(1L);
        when(cuentaDao.find(1L)).thenReturn(cuenta);

        cuentaService.depositar(1L, 500);

        assertEquals(1500, cuenta.getBalance());
        verify(cuentaDao).save(cuenta);
    }

    @Test
    public void testDepositar_Negativo() {
        assertThrows(CantidadNegativaException.class, () -> cuentaService.depositar(1L, -100));
    }

    @Test
    public void testExtraer_OK() throws Exception {
        Cuenta cuenta = buildCuenta(1L);
        when(cuentaDao.find(1L)).thenReturn(cuenta);

        cuentaService.extraer(1L, 500);

        assertEquals(500, cuenta.getBalance());
        verify(cuentaDao).save(cuenta);
    }

    @Test
    public void testExtraer_SinSaldo() {
        Cuenta cuenta = buildCuenta(1L);
        cuenta.setBalance(100);
        when(cuentaDao.find(1L)).thenReturn(cuenta);

        assertThrows(NoAlcanzaException.class, () -> cuentaService.extraer(1L, 500));
    }

    @Test
    public void testExtraer_Negativo() {
        assertThrows(CantidadNegativaException.class, () -> cuentaService.extraer(1L, -50));
    }

    @Test
    public void testTransferir_OK() throws Exception {
        Cuenta origen = buildCuenta(1L);
        Cuenta destino = buildCuenta(2L);
        origen.setBalance(1000);
        destino.setBalance(500);

        when(cuentaDao.find(1L)).thenReturn(origen);
        when(cuentaDao.find(2L)).thenReturn(destino);

        cuentaService.transferir(1L, 2L, 200);

        assertEquals(800, origen.getBalance());
        assertEquals(700, destino.getBalance());
    }

    @Test
    public void testTransferir_MismaCuenta() {
        assertThrows(TransferenciaInvalidaException.class, () -> cuentaService.transferir(1L, 1L, 100));
    }

    @Test
    public void testTransferir_TipoDistinto() {
        Cuenta origen = buildCuenta(1L);
        Cuenta destino = buildCuenta(2L);
        destino.setTipoCuenta(TipoCuenta.CUENTA_CORRIENTE);

        when(cuentaDao.find(1L)).thenReturn(origen);
        when(cuentaDao.find(2L)).thenReturn(destino);

        assertThrows(TipoCuentaNotSupportedException.class, () -> cuentaService.transferir(1L, 2L, 100));
    }

    @Test
    public void testTransferir_MonedaDistinta() {
        Cuenta origen = buildCuenta(1L);
        Cuenta destino = buildCuenta(2L);
        destino.setMoneda(TipoMoneda.DOLARES);

        when(cuentaDao.find(1L)).thenReturn(origen);
        when(cuentaDao.find(2L)).thenReturn(destino);

        assertThrows(TipoCuentaNotSupportedException.class, () -> cuentaService.transferir(1L, 2L, 100));
    }
}
