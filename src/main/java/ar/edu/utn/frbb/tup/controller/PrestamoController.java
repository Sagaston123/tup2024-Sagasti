package ar.edu.utn.frbb.tup.controller;

import ar.edu.utn.frbb.tup.controller.validator.PrestamoValidator;
import ar.edu.utn.frbb.tup.model.Prestamo;
import ar.edu.utn.frbb.tup.model.dto.PrestamoDto;
import ar.edu.utn.frbb.tup.model.exception.PrestamoNotAllowedException;
import ar.edu.utn.frbb.tup.service.PrestamoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/prestamo")
public class PrestamoController {

    @Autowired
    private PrestamoService prestamoService;

    @Autowired
    private PrestamoValidator prestamoValidator;

    @PostMapping
    public Prestamo crearPrestamo(@RequestBody PrestamoDto prestamoDto) throws PrestamoNotAllowedException {
        prestamoValidator.validate(prestamoDto);
        return prestamoService.solicitarPrestamo(prestamoDto);
    }

    @GetMapping("/{clienteId}")
    public List<Prestamo> consultarPrestamos(@PathVariable long clienteId) {
        return prestamoService.obtenerPrestamosPorCliente(clienteId);
    }
}
