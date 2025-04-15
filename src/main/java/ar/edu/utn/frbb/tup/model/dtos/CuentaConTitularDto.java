package ar.edu.utn.frbb.tup.model.dtos;

public class CuentaConTitularDto {
    private long numeroCuenta;
    private String tipoCuenta;
    private String moneda;
    private double balance;
    private String fechaCreacion;
    private TitularDto titular;

    public static class TitularDto {
        private long dni;
        private String nombre;

        public TitularDto(long dni, String nombre) {
            this.dni = dni;
            this.nombre = nombre;
        }

        public long getDni() {
            return dni;
        }

        public String getNombre() {
            return nombre;
        }
    }

    public long getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(long numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public String getTipoCuenta() {
        return tipoCuenta;
    }

    public void setTipoCuenta(String tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public TitularDto getTitular() {
        return titular;
    }

    public void setTitular(TitularDto titular) {
        this.titular = titular;
    }
}
