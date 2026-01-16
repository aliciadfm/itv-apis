package es.upv.iei.api_carga.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

@Schema(
        name = "ResultadoCarga",
        description = "Resultado agregado del proceso de carga ETL"
)
public class ResultadoCargaDTO {

    @Schema(description = "Número de registros insertados correctamente", example = "120")
    private int registrosCorrectos;

    @Schema(description = "Número de registros con errores reparados automáticamente", example = "15")
    private int registrosConErroresReparados;

    @Schema(description = "Número de registros rechazados", example = "3")
    private int registrosRechazados;

    @Schema(description = "Listado de errores reparados")
    private List<ErrorCargaDTO> erroresReparados;

    @Schema(description = "Listado de errores no reparables")
    private List<ErrorCargaDTO> erroresRechazados;

    public ResultadoCargaDTO() {
        this.registrosCorrectos = 0;
        this.registrosConErroresReparados = 0;
        this.registrosRechazados = 0;
        this.erroresReparados = new ArrayList<>();
        this.erroresRechazados = new ArrayList<>();
    }

    public void incrementarRegistrosCorrectos() {
        this.registrosCorrectos++;
    }

    public void incrementarRegistrosConErroresReparados() {
        this.registrosConErroresReparados++;
    }

    public void incrementarRegistrosRechazados() {
        this.registrosRechazados++;
    }

    public void agregarErrorReparado(ErrorCargaDTO error) {
        this.erroresReparados.add(error);
        incrementarRegistrosConErroresReparados();
    }

    public void agregarErrorRechazado(ErrorCargaDTO error) {
        this.erroresRechazados.add(error);
        incrementarRegistrosRechazados();
    }

    public void acumular(ResultadoCargaDTO other) {
        this.registrosCorrectos += other.registrosCorrectos;
        this.registrosConErroresReparados += other.registrosConErroresReparados;
        this.registrosRechazados += other.registrosRechazados;
        this.erroresReparados.addAll(other.erroresReparados);
        this.erroresRechazados.addAll(other.erroresRechazados);
    }

    public int getRegistrosCorrectos() {
        return registrosCorrectos;
    }

    public int getRegistrosConErroresReparados() {
        return registrosConErroresReparados;
    }

    public int getRegistrosRechazados() {
        return registrosRechazados;
    }

    public List<ErrorCargaDTO> getErroresReparados() {
        return erroresReparados;
    }

    public List<ErrorCargaDTO> getErroresRechazados() {
        return erroresRechazados;
    }
}