package ar.edu.utn.frbb.tup.controller.validator;


import ar.edu.utn.frbb.tup.model.dtos.CuentaDto;
import org.springframework.stereotype.Component;

@Component
public class CuentaValidator {

    public void validate(CuentaDto cuentaDto) {
        if (cuentaDto.getSaldoInicial() < 0) {
            throw new IllegalArgumentException("El saldo inicial no puede ser negativo");
        }

        if (cuentaDto.getTipoCuenta() == null || cuentaDto.getTipoCuenta().isEmpty()) {
            throw new IllegalArgumentException("El tipo de cuenta es obligatorio");
        }

        if (cuentaDto.getMoneda() == null || cuentaDto.getMoneda().isEmpty()) {
            throw new IllegalArgumentException("La moneda es obligatoria");
        }

    }
}
