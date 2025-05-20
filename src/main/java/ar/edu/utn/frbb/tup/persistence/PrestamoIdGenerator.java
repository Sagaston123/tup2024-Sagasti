package ar.edu.utn.frbb.tup.persistence;

import java.util.concurrent.atomic.AtomicLong;

public class PrestamoIdGenerator {
    private static final AtomicLong contador = new AtomicLong(1);

    public static long generarNuevoId() {
        return contador.getAndIncrement();
    }
}
