package es.upv.iei.api_carga.dto;

import java.util.List;

public class CargaRequestDTO {
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
