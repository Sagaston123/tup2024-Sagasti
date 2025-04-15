package ar.edu.utn.frbb.tup.model.dtos;

import ar.edu.utn.frbb.tup.model.TipoPersona;

public class ClienteDto extends PersonaDto{
    private TipoPersona tipoPersona;
    private String banco;

    public TipoPersona getTipoPersona() {
        return tipoPersona;
    }

    public void setTipoPersona(TipoPersona tipoPersona) {
        this.tipoPersona = tipoPersona;
    }

    public String getBanco() {
        return banco;
    }

    
    public void setBanco(String banco) {
        this.banco = banco;
    }
}
