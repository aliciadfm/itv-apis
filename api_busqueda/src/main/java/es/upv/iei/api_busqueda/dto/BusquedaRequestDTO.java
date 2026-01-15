package es.upv.iei.api_busqueda.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "BusquedaRequest",
        description = "Objeto que contiene los filtros de búsqueda de estaciones ITV"
)
public class BusquedaRequestDTO {

    @Schema(
            description = "Nombre de la localidad donde se ubica la estación",
            example = "Valencia",
            nullable = true
    )
    private String localidad;

    @Schema(
            description = "Código postal de la estación",
            example = "46001",
            nullable = true
    )
    private String codigoPostal;

    @Schema(
            description = "Nombre de la provincia",
            example = "Valencia",
            nullable = true
    )
    private String provincia;

    @Schema(
            description = """
            Tipo de estación ITV.

            Valores admitidos:
            - FIJA
            - MOVIL
            - OTROS
            """,
            example = "FIJA",
            nullable = true
    )
    private String tipo;
    // Getters y Setters
    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
