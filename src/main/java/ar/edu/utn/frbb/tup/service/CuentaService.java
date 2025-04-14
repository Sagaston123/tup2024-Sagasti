package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.dtos.CuentaDto;
import ar.edu.utn.frbb.tup.model.TipoCuenta;
import ar.edu.utn.frbb.tup.model.TipoMoneda;
import ar.edu.utn.frbb.tup.model.exception.CuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.CuentaNotFoundException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaNotSupportedException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.persistence.CuentaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class CuentaService {

    CuentaDao cuentaDao = new CuentaDao();

    @Autowired
    ClienteService clienteService;

    public void darDeAltaCuenta(Cuenta cuenta, long dniTitular)
            throws CuentaAlreadyExistsException, TipoCuentaAlreadyExistsException {

        if (cuentaDao.find(cuenta.getNumeroCuenta()) != null) {
            throw new CuentaAlreadyExistsException("La cuenta " + cuenta.getNumeroCuenta() + " ya existe.");
        }

        // Validar tipo de cuenta soportada si hace falta

        clienteService.agregarCuenta(cuenta, dniTitular);
        cuentaDao.save(cuenta);
    }

    public Cuenta crearCuenta(CuentaDto cuentaDto, long dniTitular)
        throws CuentaAlreadyExistsException, TipoCuentaAlreadyExistsException, TipoCuentaNotSupportedException {

        // Convertir Strings a Enums con validación
        TipoCuenta tipoCuenta;
        TipoMoneda tipoMoneda;
        try {
            tipoCuenta = TipoCuenta.valueOf(cuentaDto.getTipoCuenta().toUpperCase());
            tipoMoneda = TipoMoneda.valueOf(cuentaDto.getMoneda().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new TipoCuentaNotSupportedException("Tipo de cuenta o moneda inválidos.");
        }

        // Validar si está soportado
        if (!tipoDeCuentaSoportada(tipoCuenta, tipoMoneda)) {
            throw new TipoCuentaNotSupportedException("Tipo de cuenta o moneda no soportada por el banco.");
        }

        // Crear cuenta
        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(new Random().nextLong());
        cuenta.setBalance(cuentaDto.getSaldoInicial()); 
        cuenta.setFechaCreacion(LocalDateTime.now());
        cuenta.setTipoCuenta(tipoCuenta);
        cuenta.setMoneda(tipoMoneda);

        darDeAltaCuenta(cuenta, dniTitular);

        return cuenta;
    }


    public Cuenta consultarCuenta(long numeroCuenta) throws CuentaNotFoundException {
        Cuenta cuenta = cuentaDao.find(numeroCuenta);
        if (cuenta == null) {
            throw new CuentaNotFoundException("No se encontró la cuenta con número " + numeroCuenta);
        }
        return cuenta;
    }

    public List<Cuenta> listarCuentas() {
        return cuentaDao.getAll();
    }

    private boolean tipoDeCuentaSoportada(TipoCuenta tipoCuenta, TipoMoneda moneda) {
        return (tipoCuenta == TipoCuenta.CAJA_AHORRO || tipoCuenta == TipoCuenta.CUENTA_CORRIENTE)
                && (moneda == TipoMoneda.PESOS || moneda == TipoMoneda.DOLARES);
    }
    
}
