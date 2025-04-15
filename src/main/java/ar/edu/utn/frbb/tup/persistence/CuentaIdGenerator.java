package ar.edu.utn.frbb.tup.persistence;

public class CuentaIdGenerator {
    private static long contador = 1;

    public static synchronized long generarNuevoId() {
        return contador++;
    }
}
