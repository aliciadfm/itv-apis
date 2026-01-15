package es.upv.iei.api_carga.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(
        name = "CargaRequest",
        description = "Identifica las fuentes de datos a cargar en el proceso ETL"
)
public class CargaRequestDTO {

    @Schema(
            description = "Listado de fuentes de datos a cargar",
            example = "[\"CV\", \"CAT\", \"GAL\"]"
    )
    private List<String> fuentes;

    public CargaRequestDTO() {
    }

    public List<String> getFuentes() {
        return fuentes;
    }

    public void setFuentes(List<String> fuentes) {
        this.fuentes = fuentes;
    }
}
