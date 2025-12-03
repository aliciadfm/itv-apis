package es.upv.iei.api_busqueda.dto;

public class ProvinciaDTO {

    private Long codigo;
    private String nombre;

    public ProvinciaDTO() {}

    public ProvinciaDTO(Long codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
