package ar.edu.utn.frbb.tup.persistence;

import ar.edu.utn.frbb.tup.model.Cliente;
import ar.edu.utn.frbb.tup.persistence.entity.ClienteEntity;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ClienteDao extends AbstractBaseDao {

    public Cliente find(long dni, boolean loadComplete) {
        if (getInMemoryDatabase().get(dni) == null)
            return null;
        // Solo devuelve el cliente, no carga cuentas
        return ((ClienteEntity) getInMemoryDatabase().get(dni)).toCliente();
    }

    public void save(Cliente cliente) {
        ClienteEntity entity = new ClienteEntity(cliente);
        getInMemoryDatabase().put(entity.getId(), entity);
    }

    public List<Cliente> getAll() {
        return getInMemoryDatabase().values().stream()
                .map(obj -> ((ClienteEntity) obj).toCliente())
                .toList();
    }

    @Override
    protected String getEntityName() {
        return "CLIENTE";
    }
}