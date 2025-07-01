package ar.edu.utn.frbb.tup.controller;

import ar.edu.utn.frbb.tup.controller.validator.CuentaValidator;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.dtos.CuentaConTitularDto;
import ar.edu.utn.frbb.tup.model.dtos.CuentaDto;
import ar.edu.utn.frbb.tup.model.exception.CantidadNegativaException;
import ar.edu.utn.frbb.tup.model.exception.ClienteNotFoundException;
import ar.edu.utn.frbb.tup.model.exception.CuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.CuentaNotFoundException;
import ar.edu.utn.frbb.tup.model.exception.NoAlcanzaException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaNotSupportedException;
import ar.edu.utn.frbb.tup.model.exception.TransferenciaInvalidaException;
import ar.edu.utn.frbb.tup.service.CuentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cuenta")
public class CuentaController {

    @Autowired
    private CuentaService cuentaService;

    @Autowired
    private CuentaValidator cuentaValidator;

    @PostMapping
    public Cuenta crearCuenta(@RequestBody CuentaDto cuentaDto)
            throws CuentaAlreadyExistsException, TipoCuentaAlreadyExistsException, TipoCuentaNotSupportedException, ClienteNotFoundException {
        cuentaValidator.validate(cuentaDto);
        return cuentaService.crearCuenta(cuentaDto, cuentaDto.getClienteId());
    }


    @GetMapping("/{numeroCuenta}")
    public ResponseEntity<CuentaConTitularDto> consultarCuenta(@PathVariable long numeroCuenta) {
        try {
            CuentaConTitularDto dto = cuentaService.consultarCuentaConTitular(numeroCuenta);
            return ResponseEntity.ok(dto);
        } catch (CuentaNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{numeroCuenta}/depositar")
    public ResponseEntity<String> depositar(@PathVariable long numeroCuenta, @RequestParam double monto) {
        try {
            cuentaService.depositar(numeroCuenta, monto);
            Cuenta cuenta = cuentaService.consultarCuenta(numeroCuenta);
            return ResponseEntity.ok("Se depositó " + monto + " " + cuenta.getMoneda() + " a la cuenta " + numeroCuenta);
        } catch (CuentaNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cuenta no encontrada");
        } catch (CantidadNegativaException e) {
            return ResponseEntity.badRequest().body("El monto debe ser positivo");
        }
    }
    
    @PutMapping("/{numeroCuenta}/extraer")
    public ResponseEntity<String> extraer(@PathVariable long numeroCuenta, @RequestParam double monto) {
        try {
            cuentaService.extraer(numeroCuenta, monto);
            Cuenta cuenta = cuentaService.consultarCuenta(numeroCuenta);
            return ResponseEntity.ok("Se extrajeron " + monto + " " + cuenta.getMoneda() + " de la cuenta " + numeroCuenta);
        } catch (CuentaNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cuenta no encontrada");
        } catch (CantidadNegativaException e) {
            return ResponseEntity.badRequest().body("El monto debe ser positivo");
        } catch (NoAlcanzaException e) {
            return ResponseEntity.badRequest().body("Saldo insuficiente");
        }
    }

    @PutMapping("/transferir")
    public ResponseEntity<String> transferir(@RequestParam long origen, @RequestParam long destino, @RequestParam double monto) {
        try {
            cuentaService.transferir(origen, destino, monto);
            Cuenta cuentaOrigen = cuentaService.consultarCuenta(origen);
            return ResponseEntity.ok("Se transfirieron " + monto + " " + cuentaOrigen.getMoneda() +
                    " de la cuenta " + origen + " a la cuenta " + destino);
        } catch (CuentaNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cuenta no encontrada");
        } catch (CantidadNegativaException e) {
            return ResponseEntity.badRequest().body("El monto debe ser positivo");
        } catch (NoAlcanzaException e) {
            return ResponseEntity.badRequest().body("Saldo insuficiente");
        } catch (TipoCuentaNotSupportedException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (TransferenciaInvalidaException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); 
        }
    }

    @GetMapping
    public List<Cuenta> listarCuentas() {
        return cuentaService.listarCuentas();
    }
}
