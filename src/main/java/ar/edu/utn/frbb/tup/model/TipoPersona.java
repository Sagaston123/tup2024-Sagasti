package ar.edu.utn.frbb.tup.model;

public enum TipoPersona {

    PERSONA_FISICA("F"),
    PERSONA_JURIDICA("J");

    private final String descripcion;

    TipoPersona(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public static TipoPersona fromString(String text) {
        for (TipoPersona tipo : TipoPersona.values()) {
            if (tipo.name().equalsIgnoreCase(text) || tipo.descripcion.equalsIgnoreCase(text)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("No se pudo encontrar un TipoPersona con el texto: " + text);
    }
}
