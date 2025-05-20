package ar.edu.utn.frbb.tup.model.exception;

public class ClienteNotFoundException extends RuntimeException {
    public ClienteNotFoundException() {
        super("Cliente no encontrado.");
    }
}