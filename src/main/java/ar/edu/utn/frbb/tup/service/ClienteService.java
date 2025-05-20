package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.model.dtos.ClienteDto;
import ar.edu.utn.frbb.tup.model.Cliente;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.exception.ClienteAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.ClienteNotFoundException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.persistence.ClienteDao;
import ar.edu.utn.frbb.tup.persistence.CuentaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteDao clienteDao;
    @Autowired
    private CuentaDao cuentaDao;

    public ClienteService(ClienteDao clienteDao) {
        this.clienteDao = clienteDao;
    }

    public Cliente darDeAltaCliente(ClienteDto clienteDto) throws ClienteAlreadyExistsException {
        Cliente cliente = new Cliente(clienteDto);

        if (clienteDao.find(cliente.getDni(), false) != null) {
            throw new ClienteAlreadyExistsException("Ya existe un cliente con DNI " + cliente.getDni());
        }

        if (cliente.getEdad() < 18) {
            throw new IllegalArgumentException("El cliente debe ser mayor a 18 años");
        }

        clienteDao.save(cliente);
        return cliente;
    }

    public void validarCuentaNueva(Cuenta cuenta, long dniTitular) throws TipoCuentaAlreadyExistsException, ClienteNotFoundException {
        // Validar que el cliente exista
        if (clienteDao.find(dniTitular, false) == null) {
            throw new ClienteNotFoundException();
        }

        // Validar que no tenga otra cuenta igual (tipo y moneda)
        boolean yaTiene = cuentaDao.getAll().stream()
            .anyMatch(c -> c.getDniTitular() == dniTitular
                && c.getTipoCuenta() == cuenta.getTipoCuenta()
                && c.getMoneda() == cuenta.getMoneda());

        if (yaTiene) {
            throw new TipoCuentaAlreadyExistsException("El cliente ya posee una cuenta de ese tipo y moneda");
        }
    }

    public Cliente buscarClientePorDni(long dni) throws ClienteNotFoundException {
        Cliente cliente = clienteDao.find(dni, true);
        if (cliente == null) {
            throw new ClienteNotFoundException();
        }
        return cliente;
    }

    public List<Cliente> listarTodos() {
        return clienteDao.getAll();
    }
}