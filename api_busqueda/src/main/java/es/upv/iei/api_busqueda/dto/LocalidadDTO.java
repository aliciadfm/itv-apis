package es.upv.iei.api_busqueda.dto;

public class LocalidadDTO {

    private Long codigo;
    private String nombre;
    private Long provinciaCodigo;
    private String provinciaNombre;

    public LocalidadDTO() {}

    public LocalidadDTO(Long codigo, String nombre, Long provinciaCodigo, String provinciaNombre) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.provinciaCodigo = provinciaCodigo;
        this.provinciaNombre = provinciaNombre;
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

    public Long getProvinciaCodigo() {
        return provinciaCodigo;
    }

    public void setProvinciaCodigo(Long provinciaCodigo) {
        this.provinciaCodigo = provinciaCodigo;
    }

    public String getProvinciaNombre() {
        return provinciaNombre;
    }

    public void setProvinciaNombre(String provinciaNombre) {
        this.provinciaNombre = provinciaNombre;
    }
}
