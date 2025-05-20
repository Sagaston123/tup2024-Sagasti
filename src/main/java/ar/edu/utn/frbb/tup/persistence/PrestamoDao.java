package ar.edu.utn.frbb.tup.persistence;

import ar.edu.utn.frbb.tup.model.Prestamo;
import ar.edu.utn.frbb.tup.persistence.entity.PrestamoEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class PrestamoDao extends AbstractBaseDao {
    @Override
    protected String getEntityName() {
        return "PRESTAMO";
    }

    public void save(Prestamo prestamo) {
        PrestamoEntity entity = new PrestamoEntity(prestamo);
        getInMemoryDatabase().put(entity.getId(), entity);
    }

    public List<Prestamo> findByClienteId(long clienteId) {
        return getInMemoryDatabase().values().stream()
                .map(p -> ((PrestamoEntity) p).toPrestamo())
                .filter(p -> p.getClienteId() == clienteId) 
                .collect(Collectors.toList());
    }
}