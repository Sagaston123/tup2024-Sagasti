package ar.edu.utn.frbb.tup.controller;

import ar.edu.utn.frbb.tup.controller.validator.ClienteValidator;
import ar.edu.utn.frbb.tup.model.Cliente;
import ar.edu.utn.frbb.tup.model.dto.ClienteDto;
import ar.edu.utn.frbb.tup.model.exception.ClienteAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.ClienteNotFoundException;
import ar.edu.utn.frbb.tup.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cliente")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ClienteValidator clienteValidator;

    @PostMapping
    public Cliente crearCliente(@RequestBody ClienteDto clienteDto) throws ClienteAlreadyExistsException {
        clienteValidator.validate(clienteDto);
        return clienteService.darDeAltaCliente(clienteDto);
    }

    @GetMapping("/{dni}")
    public Cliente buscarClientePorDni(@PathVariable long dni) throws ClienteNotFoundException {
        return clienteService.buscarPorDni(dni);
    }

    @GetMapping
    public List<Cliente> listarClientes() {
        return clienteService.listarTodos();
    }
}
