package ar.edu.utn.frbb.tup.persistence.entity;

import ar.edu.utn.frbb.tup.model.Cliente;
import ar.edu.utn.frbb.tup.model.TipoPersona;

import java.time.LocalDate;

public class ClienteEntity extends BaseEntity {

    private final String tipoPersona;
    private final String nombre;
    private final String apellido;
    private final String banco;
    private final LocalDate fechaAlta;
    private final LocalDate fechaNacimiento;

    public ClienteEntity(Cliente cliente) {
        super(cliente.getDni());
        this.tipoPersona = cliente.getTipoPersona() != null ? cliente.getTipoPersona().getDescripcion() : null;
        this.nombre = cliente.getNombre();
        this.apellido = cliente.getApellido();
        this.banco = cliente.getBanco();
        this.fechaAlta = cliente.getFechaAlta();
        this.fechaNacimiento = cliente.getFechaNacimiento();
    }

    public Cliente toCliente() {
        Cliente cliente = new Cliente();
        cliente.setDni(this.getId());
        cliente.setNombre(this.nombre);
        cliente.setApellido(this.apellido);
        cliente.setTipoPersona(TipoPersona.fromString(this.tipoPersona));
        cliente.setBanco(this.banco);
        cliente.setFechaAlta(this.fechaAlta);
        cliente.setFechaNacimiento(this.fechaNacimiento);
        return cliente;
    }
}