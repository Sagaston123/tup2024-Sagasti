package ar.edu.utn.frbb.tup.model.exception;

public class CantidadNegativaException extends RuntimeException {
    public CantidadNegativaException( ) {
        super("La cantidad no puede ser negativa" );
    }
}
