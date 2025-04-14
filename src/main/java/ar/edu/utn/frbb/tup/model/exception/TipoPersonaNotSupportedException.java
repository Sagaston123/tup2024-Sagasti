package ar.edu.utn.frbb.tup.model.exception;

public class TipoPersonaNotSupportedException extends Exception {
    public TipoPersonaNotSupportedException() {
        super("Tipo de persona no soportado");
    }
}
