package ar.edu.utn.frbb.tup.controller;

import ar.edu.utn.frbb.tup.controller.validator.PrestamoValidator;
import ar.edu.utn.frbb.tup.model.Prestamo;
import ar.edu.utn.frbb.tup.model.dtos.PrestamoDto;
import ar.edu.utn.frbb.tup.model.dtos.PrestamosClienteResponse;
import ar.edu.utn.frbb.tup.model.exception.PrestamoNotAllowedException;
import ar.edu.utn.frbb.tup.service.PrestamoService;
import ar.edu.utn.frbb.tup.model.dtos.SolicitudPrestamoResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/prestamo")
public class PrestamoController {

    @Autowired
    private PrestamoService prestamoService;

    @Autowired
    private PrestamoValidator prestamoValidator;

    @PostMapping
    public SolicitudPrestamoResponse crearPrestamo(@RequestBody PrestamoDto prestamoDto) throws PrestamoNotAllowedException {
        prestamoValidator.validate(prestamoDto);
        return prestamoService.solicitarPrestamo(prestamoDto);
    }


    @GetMapping("/{clienteId}")
    public PrestamosClienteResponse consultarPrestamos(@PathVariable long clienteId) {
        List<Prestamo> prestamos = prestamoService.obtenerPrestamosPorCliente(clienteId);
        List<PrestamosClienteResponse.PrestamoResumenDto> resumen = prestamos.stream()
            .map(p -> new PrestamosClienteResponse.PrestamoResumenDto(
                p.getMonto(),
                p.getPlazoMeses(),
                p.getPagosRealizados(),
                p.getSaldoRestante()
            )).toList();
        return new PrestamosClienteResponse(clienteId, resumen);
    }
}
