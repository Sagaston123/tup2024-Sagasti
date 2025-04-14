package ar.edu.utn.frbb.tup.service;

import org.springframework.stereotype.Service;

@Service
public class CreditScoringService {
    // Simulación del servicio externo
    public boolean tieneBuenHistorial(long dniCliente) {
        return dniCliente % 2 == 0;
    }
}
