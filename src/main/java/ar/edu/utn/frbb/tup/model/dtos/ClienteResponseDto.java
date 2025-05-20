package ar.edu.utn.frbb.tup.model.dtos;

import ar.edu.utn.frbb.tup.model.Cliente;

import java.util.List;

public class ClienteResponseDto {
    private String nombre;
    private String apellido;
    private long dni;
    private String banco;
    private String tipoPersona;
    private List<Long> cuentas;

    public ClienteResponseDto(Cliente cliente, List<Long> cuentas) {
        this.nombre = cliente.getNombre();
        this.apellido = cliente.getApellido();
        this.dni = cliente.getDni();
        this.banco = cliente.getBanco();
        this.tipoPersona = cliente.getTipoPersona() != null ? cliente.getTipoPersona().name() : null;
        this.cuentas = cuentas;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public long getDni() {
        return dni;
    }
    
    public void setDni(long dni) {
        this.dni = dni;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getTipoPersona() {
        return tipoPersona;
    }

    public void setTipoPersona(String tipoPersona) {
        this.tipoPersona = tipoPersona;
    }

    public List<Long> getCuentas() {
        return cuentas;
    }

    public void setCuentas(List<Long> cuentas) {
        this.cuentas = cuentas;
    }
    
}