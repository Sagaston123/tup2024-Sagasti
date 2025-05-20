package ar.edu.utn.frbb.tup.persistence.entity;

import ar.edu.utn.frbb.tup.model.Prestamo;
import ar.edu.utn.frbb.tup.model.TipoMoneda;

import java.time.LocalDate;

public class PrestamoEntity extends BaseEntity {
    private long clienteId;
    private double monto;
    private int plazoMeses;
    private int pagosRealizados;
    private double saldoRestante;
    private String moneda;
    private String banco;
    private LocalDate fecha;

    public PrestamoEntity(Prestamo prestamo) {
        super(prestamo.getId());
        this.clienteId = prestamo.getClienteId();
        this.monto = prestamo.getMonto();
        this.plazoMeses = prestamo.getPlazoMeses();
        this.pagosRealizados = prestamo.getPagosRealizados();
        this.saldoRestante = prestamo.getSaldoRestante();
        this.moneda = prestamo.getMoneda().toString();
        this.banco = prestamo.getBanco();
        this.fecha = prestamo.getFecha();
    }

    public Prestamo toPrestamo() {
        Prestamo p = new Prestamo();
        p.setId(getId());
        p.setClienteId(this.clienteId);
        p.setMonto(monto);
        p.setPlazoMeses(plazoMeses);
        p.setPagosRealizados(pagosRealizados);
        p.setSaldoRestante(saldoRestante);
        p.setMoneda(TipoMoneda.valueOf(moneda));
        p.setBanco(banco);
        p.setFecha(fecha);
        return p;
    }

    //getters y setters
    public String getBanco() {
        return banco;
    }
    public void setBanco(String banco) {
        this.banco = banco;
    }
    public long getClienteId() {
        return clienteId;
    }
    public void setClienteId(long clienteId) {
        this.clienteId = clienteId;
    }
    public double getMonto() {
        return monto;
    }
    public void setMonto(double monto) {
        this.monto = monto;
    }
    public int getPlazoMeses() {
        return plazoMeses;
    }
    public void setPlazoMeses(int plazoMeses) {
        this.plazoMeses = plazoMeses;
    }
    public int getPagosRealizados() {
        return pagosRealizados;
    }
    public void setPagosRealizados(int pagosRealizados) {
        this.pagosRealizados = pagosRealizados;
    }
    public double getSaldoRestante() {
        return saldoRestante;
    }
    public void setSaldoRestante(double saldoRestante) {
        this.saldoRestante = saldoRestante;
    }
    public String getMoneda() {
        return moneda;
    }
    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }
    public LocalDate getFecha() {
        return fecha;
    }
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
}
