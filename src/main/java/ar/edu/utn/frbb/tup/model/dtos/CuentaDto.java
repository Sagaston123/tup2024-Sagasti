package ar.edu.utn.frbb.tup.model.dto;


public class CuentaDto {
    private long clienteId;
    private String tipoCuenta;
    private String moneda;
    private double saldoInicial;

    // Getters y Setters
    public long getClienteId() {
        return clienteId;
    }

    public void setClienteId(long clienteId) {
        this.clienteId = clienteId;
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

    public double getSaldoInicial() {
        return saldoInicial;
    }

    public void setSaldoInicial(double saldoInicial) {
        this.saldoInicial = saldoInicial;
    }

}
