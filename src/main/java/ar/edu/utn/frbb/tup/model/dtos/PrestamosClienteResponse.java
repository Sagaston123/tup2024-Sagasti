package ar.edu.utn.frbb.tup.model.dtos;

import java.util.List;

public class PrestamosClienteResponse {
    private long numeroCliente;
    private List<PrestamoResumenDto> prestamos;

    public PrestamosClienteResponse(long numeroCliente, List<PrestamoResumenDto> prestamos) {
        this.numeroCliente = numeroCliente;
        this.prestamos = prestamos;
    }

    public long getNumeroCliente() {
        return numeroCliente;
    }

    public List<PrestamoResumenDto> getPrestamos() {
        return prestamos;
    }

    public static class PrestamoResumenDto {
        private double monto;
        private int plazoMeses;
        private int pagosRealizados;
        private double saldoRestante;

        public PrestamoResumenDto(double monto, int plazoMeses, int pagosRealizados, double saldoRestante) {
            this.monto = monto;
            this.plazoMeses = plazoMeses;
            this.pagosRealizados = pagosRealizados;
            this.saldoRestante = saldoRestante;
        }

        public double getMonto() {
            return monto;
        }

        public int getPlazoMeses() {
            return plazoMeses;
        }

        public int getPagosRealizados() {
            return pagosRealizados;
        }

        public double getSaldoRestante() {
            return saldoRestante;
        }
    }
}
