package es.upv.iei.api_carga.dto;

import java.util.ArrayList;
import java.util.List;

public class ResultadoCargaDTO {
    private int registrosCorrectos;
    private int registrosConErroresReparados;
    private int registrosRechazados;
    private List<ErrorCargaDTO> erroresReparados;
    private List<ErrorCargaDTO> erroresRechazados;

    public ResultadoCargaDTO() {
        this.registrosCorrectos = 0;
        this.registrosConErroresReparados = 0;
        this.registrosRechazados = 0;
        this.erroresReparados = new ArrayList<>();
        this.erroresRechazados = new ArrayList<>();
    }

    public int getRegistrosCorrectos() {
        return registrosCorrectos;
    }

    public void setRegistrosCorrectos(int registrosCorrectos) {
        this.registrosCorrectos = registrosCorrectos;
    }

    public void incrementarRegistrosCorrectos() {
        this.registrosCorrectos++;
    }

    public int getRegistrosConErroresReparados() {
        return registrosConErroresReparados;
    }

    public void setRegistrosConErroresReparados(int registrosConErroresReparados) {
        this.registrosConErroresReparados = registrosConErroresReparados;
    }

    public void incrementarRegistrosConErroresReparados() {
        this.registrosConErroresReparados++;
    }

    public int getRegistrosRechazados() {
        return registrosRechazados;
    }

    public void setRegistrosRechazados(int registrosRechazados) {
        this.registrosRechazados = registrosRechazados;
    }

    public void incrementarRegistrosRechazados() {
        this.registrosRechazados++;
    }

    public List<ErrorCargaDTO> getErroresReparados() {
        return erroresReparados;
    }

    public void setErroresReparados(List<ErrorCargaDTO> erroresReparados) {
        this.erroresReparados = erroresReparados;
    }

    public void agregarErrorReparado(ErrorCargaDTO error) {
        this.erroresReparados.add(error);
        incrementarRegistrosConErroresReparados();
    }

    public List<ErrorCargaDTO> getErroresRechazados() {
        return erroresRechazados;
    }

    public void setErroresRechazados(List<ErrorCargaDTO> erroresRechazados) {
        this.erroresRechazados = erroresRechazados;
    }

    public void agregarErrorRechazado(ErrorCargaDTO error) {
        this.erroresRechazados.add(error);
        incrementarRegistrosRechazados();
    }
}
