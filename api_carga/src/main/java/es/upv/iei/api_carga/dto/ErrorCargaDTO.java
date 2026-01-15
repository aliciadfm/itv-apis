package es.upv.iei.api_carga.dto;

public class ErrorCargaDTO {
    private String fuente;
    private String nombre;
    private String localidad;
    private String motivo;
    private String operacion;

    public ErrorCargaDTO() {
    }

    public ErrorCargaDTO(String fuente, String nombre, String localidad, String motivo, String operacion) {
        this.fuente = fuente;
        this.nombre = nombre;
        this.localidad = localidad;
        this.motivo = motivo;
        this.operacion = operacion;
    }

    public String getFuente() {
        return fuente;
    }

    public void setFuente(String fuente) {
        this.fuente = fuente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getOperacion() {
        return operacion;
    }

    public void setOperacion(String operacion) {
        this.operacion = operacion;
    }
}
