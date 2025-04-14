package ar.edu.utn.frbb.tup.model.exception;

public class ClienteNotFoundException extends Exception {
    public ClienteNotFoundException() {
        super("Cliente no encontrado.");
    }
}