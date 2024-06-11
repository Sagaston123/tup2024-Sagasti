package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.model.*;
import ar.edu.utn.frbb.tup.model.exception.ClienteAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.CuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.TipoDeCuentaNoSoportadaException;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CuentaServiceTest {
    
    @Mock
    private CuentaDao cuentaDao;

    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private CuentaService cuentaService;

    @BeforeAll
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test   //    4 - cuenta creada exitosamente
    public void testCuentaSucess() throws CuentaAlreadyExistsException, TipoCuentaAlreadyExistsException, TipoDeCuentaNoSoportadaException{
        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(123654);
        long dniTitular = 48357988;
        
        when(cuentaDao.find(cuenta.getNumeroCuenta())).thenReturn(null); 

        cuentaService.darDeAltaCuenta(cuenta, dniTitular);
        
        verify(cuentaDao, times(1)).save(cuenta);
    }

    @Test   //    1 - cuenta existente 
    public void testCuentaAlreadyExists() throws CuentaAlreadyExistsException {
        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(999);
        long dniTitular = 12345678;

        when(cuentaDao.find(cuenta.getNumeroCuenta())).thenReturn(new Cuenta());    //Retorna Cuenta porque debe indicar que la cuenta ya existe

        assertThrows(CuentaAlreadyExistsException.class, () -> cuentaService.darDeAltaCuenta(cuenta, dniTitular));
    
        verify(cuentaDao, never()).save(any(Cuenta.class)); //Verifico que nunca se haya llamado el metodo save
    }

    @Test // 2 - cuenta no supported
    public void testCuentaNoSoportada() {
        Cuenta cuentaNoSoportada = new Cuenta();
        cuentaNoSoportada.setTipoCuenta(TipoCuenta.CUENTA_CORRIENTE);   
        cuentaNoSoportada.setMoneda(TipoMoneda.DOLARES);

        when(cuentaDao.find(cuentaNoSoportada.getNumeroCuenta())).thenReturn(null);

        assertThrows(TipoDeCuentaNoSoportadaException.class, () -> cuentaService.darDeAltaCuenta(cuentaNoSoportada, 123L));
    }

   @Test    //    3 - cliente ya tiene cuenta de ese tipo
   public void testClienteAlreadyHasCuentaType() throws TipoCuentaAlreadyExistsException, CuentaAlreadyExistsException, TipoDeCuentaNoSoportadaException{
        long dniTitular = 123;
        Cuenta cuenta = new Cuenta()
            .setMoneda(TipoMoneda.PESOS)
            .setBalance(500000)
            .setTipoCuenta(TipoCuenta.CAJA_AHORRO);
        cuenta.setNumeroCuenta(112312);

        when(cuentaDao.find(cuenta.getNumeroCuenta())).thenReturn(null);

        cuentaService.darDeAltaCuenta(cuenta, dniTitular);

        Cuenta cuenta2 = new Cuenta()
                .setMoneda(TipoMoneda.PESOS)
                .setBalance(500000)
                .setTipoCuenta(TipoCuenta.CAJA_AHORRO);
        cuenta2.setNumeroCuenta(321);

            //PREGUNTAR
        //doThrow(TipoCuentaAlreadyExistsException.class).when(clienteService).agregarCuenta(cuenta2, dniTitular);
        //when(clienteService.agregarCuenta(cuenta2, dniTitular)).thenReturn(TipoCuentaAlreadyExistsException.class);
        assertThrows(TipoCuentaAlreadyExistsException.class, () -> cuentaService.darDeAltaCuenta(cuenta2, dniTitular));
    
        verify(cuentaDao, never()).save(cuenta2);

    /* long dniTitular = 12333333;

    // Crear una cuenta y simular que ya existe
    Cuenta cuentaExistente = new Cuenta()
            .setMoneda(TipoMoneda.PESOS)
            .setTipoCuenta(TipoCuenta.CAJA_AHORRO);
    cuentaExistente.setNumeroCuenta(123456);

    when(cuentaDao.find(cuentaExistente.getNumeroCuenta())).thenReturn(new Cuenta());

    // Verificar que agregar una cuenta existente arroja una excepción
    assertThrows(TipoCuentaAlreadyExistsException.class, () -> cuentaService.darDeAltaCuenta(cuentaExistente, dniTitular));

    // Verificar que el método save no fue invocado
    verify(cuentaDao, never()).save(any(Cuenta.class));
        

    } */
    }
}
