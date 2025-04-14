package ar.edu.utn.frbb.tup.model.exception;

public class TipoMonedaNotSupportedException extends  Exception{
    public TipoMonedaNotSupportedException() {
        super("Tipo de moneda no soportado");
    }
}
