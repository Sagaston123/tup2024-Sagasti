package ar.edu.utn.frbb.tup.controller.validator;

import ar.edu.utn.frbb.tup.model.dto.PrestamoDto;
import org.springframework.stereotype.Component;

@Component
public class PrestamoValidator {

    public void validate(PrestamoDto prestamoDto) {
        if (prestamoDto.getMonto() <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a 0");
        }

        if (prestamoDto.getPlazoMeses() <= 0) {
            throw new IllegalArgumentException("El plazo debe ser mayor a 0");
        }

        if (prestamoDto.getMoneda() == null || prestamoDto.getMoneda().isEmpty()) {
            throw new IllegalArgumentException("La moneda es obligatoria");
        }

        if (prestamoDto.getNumeroCliente() <= 0) {
            throw new IllegalArgumentException("Número de cliente inválido");
        }

        // Podés agregar otras validaciones, como si el monto supera un límite permitido
    }
}
