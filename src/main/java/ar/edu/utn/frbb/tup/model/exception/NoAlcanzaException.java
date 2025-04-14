package ar.edu.utn.frbb.tup.model.exception;

public class NoAlcanzaException extends RuntimeException {
    public NoAlcanzaException( ) {
        super("No alcanza el monto para realizar la operación.");
    }
}
