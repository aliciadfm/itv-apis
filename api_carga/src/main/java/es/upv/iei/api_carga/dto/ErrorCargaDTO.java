package es.upv.iei.api_carga.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "ErrorCarga",
        description = "Describe un error detectado durante el proceso de carga"
)
public class ErrorCargaDTO {

    @Schema(description = "Fuente de datos donde se produjo el error", example = "CAT")
    private String fuente;

    @Schema(description = "Nombre de la estación afectada", example = "ITV Barcelona")
    private String nombre;

    @Schema(description = "Localidad asociada al registro", example = "Barcelona")
    private String localidad;

    @Schema(description = "Motivo del error detectado", example = "Código postal inválido")
    private String motivo;

    @Schema(description = "Operación realizada sobre el registro", example = "RECHAZADO")
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
