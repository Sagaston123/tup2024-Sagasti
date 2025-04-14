package ar.edu.utn.frbb.tup.model.dto;

import java.util.List;

public class SolicitudPrestamoResponse {
    private String estado;
    private String mensaje;
    private List<PlanPagoDto> planPagos;

    public SolicitudPrestamoResponse(String estado, String mensaje, List<PlanPagoDto> planPagos) {
        this.estado = estado;
        this.mensaje = mensaje;
        this.planPagos = planPagos;
    }

    public String getEstado() {
        return estado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public List<PlanPagoDto> getPlanPagos() {
        return planPagos;
    }
}