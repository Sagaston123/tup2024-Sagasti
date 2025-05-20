package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.model.Cliente;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.dtos.CuentaConTitularDto;
import ar.edu.utn.frbb.tup.model.dtos.CuentaDto;
import ar.edu.utn.frbb.tup.model.TipoCuenta;
import ar.edu.utn.frbb.tup.model.TipoMoneda;
import ar.edu.utn.frbb.tup.persistence.CuentaIdGenerator;
import ar.edu.utn.frbb.tup.model.exception.CantidadNegativaException;
import ar.edu.utn.frbb.tup.model.exception.ClienteNotFoundException;
import ar.edu.utn.frbb.tup.model.exception.CuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.CuentaNotFoundException;
import ar.edu.utn.frbb.tup.model.exception.NoAlcanzaException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaNotSupportedException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.persistence.ClienteDao;
import ar.edu.utn.frbb.tup.persistence.CuentaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CuentaService {

    @Autowired
    private CuentaDao cuentaDao;

    @Autowired
    private ClienteDao clienteDao;

    @Autowired
    private ClienteService clienteService;

    public void darDeAltaCuenta(Cuenta cuenta, long dniTitular)
            throws CuentaAlreadyExistsException, TipoCuentaAlreadyExistsException, ClienteNotFoundException {

        if (cuentaDao.find(cuenta.getNumeroCuenta()) != null) {
            throw new CuentaAlreadyExistsException("La cuenta " + cuenta.getNumeroCuenta() + " ya existe.");
        }

        // Validar reglas de negocio del cliente antes de guardar la cuenta
        clienteService.validarCuentaNueva(cuenta, dniTitular);

        cuentaDao.save(cuenta);
    }

    public Cuenta crearCuenta(CuentaDto cuentaDto, long dniTitular)
        throws CuentaAlreadyExistsException, TipoCuentaAlreadyExistsException, TipoCuentaNotSupportedException, ClienteNotFoundException {

        // Validar que el cliente exista
        Cliente titular = clienteDao.find(dniTitular, false);
        if (titular == null) {
            throw new ClienteNotFoundException();
        }

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
        cuenta.setNumeroCuenta(CuentaIdGenerator.generarNuevoId());
        cuenta.setBalance(cuentaDto.getSaldoInicial());
        cuenta.setFechaCreacion(LocalDateTime.now());
        cuenta.setTipoCuenta(tipoCuenta);
        cuenta.setMoneda(tipoMoneda);
        cuenta.setDniTitular(dniTitular);

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

    public CuentaConTitularDto consultarCuentaConTitular(long numeroCuenta) throws CuentaNotFoundException {
        Cuenta cuenta = consultarCuenta(numeroCuenta);

        CuentaConTitularDto dto = new CuentaConTitularDto();
        dto.setNumeroCuenta(cuenta.getNumeroCuenta());
        dto.setTipoCuenta(cuenta.getTipoCuenta().name());
        dto.setMoneda(cuenta.getMoneda().name());
        dto.setBalance(cuenta.getBalance());
        dto.setFechaCreacion(cuenta.getFechaCreacion().toString());
        dto.setTitular(new CuentaConTitularDto.TitularDto(cuenta.getDniTitular(),  "" ));
        return dto;
    }

    public void depositar(long numeroCuenta, double monto) throws CuentaNotFoundException, CantidadNegativaException {
        if (monto < 0) {
            throw new CantidadNegativaException();
        }
        Cuenta cuenta = consultarCuenta(numeroCuenta);
        cuenta.setBalance(cuenta.getBalance() + monto);
        cuentaDao.save(cuenta);
    }

    public void extraer(long numeroCuenta, double monto) throws CuentaNotFoundException, CantidadNegativaException, NoAlcanzaException {
        if (monto < 0) {
            throw new CantidadNegativaException();
        }
        Cuenta cuenta = consultarCuenta(numeroCuenta);
        if (cuenta.getBalance() < monto) {
            throw new NoAlcanzaException();
        }
        cuenta.setBalance(cuenta.getBalance() - monto);
        cuentaDao.save(cuenta);
    }

    public void transferir(long origen, long destino, double monto) throws CuentaNotFoundException, CantidadNegativaException, NoAlcanzaException, TipoCuentaNotSupportedException {
        Cuenta cuentaOrigen = consultarCuenta(origen);
        Cuenta cuentaDestino = consultarCuenta(destino);
        if (monto < 0) {
            throw new CantidadNegativaException();
        }
        if (cuentaOrigen.getTipoCuenta() != cuentaDestino.getTipoCuenta()) {
            throw new TipoCuentaNotSupportedException("Las cuentas deben ser del mismo tipo para transferir.");
        }
        if (cuentaOrigen.getMoneda() != cuentaDestino.getMoneda()) {
            throw new TipoCuentaNotSupportedException("Las cuentas deben ser de la misma moneda para transferir.");
        }

        extraer(origen, monto);
        depositar(destino, monto);
    }
}