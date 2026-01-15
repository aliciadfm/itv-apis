package es.upv.iei.api_carga.service;

import com.fasterxml.jackson.databind.JsonNode;
import es.upv.iei.api_carga.dto.ResultadoCargaDTO;
import es.upv.iei.api_carga.model.Estacion;
import es.upv.iei.api_carga.model.Localidad;
import es.upv.iei.api_carga.model.Provincia;
import es.upv.iei.api_carga.model.Tipo;
import es.upv.iei.api_carga.repository.EstacionRepository;
import es.upv.iei.api_carga.repository.LocalidadRepository;
import es.upv.iei.api_carga.repository.ProvinciaRepository;
import es.upv.iei.api_carga.wrapper.WrapperCSV;
import es.upv.iei.api_carga.wrapper.WrapperJSON;
import es.upv.iei.api_carga.wrapper.WrapperXML;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CargaService {

    private final WrapperJSON wrapperJSON;
    private final WrapperXML wrapperXML;
    private final WrapperCSV wrapperCSV;
    private final EstacionRepository estacionRepository;
    private final LocalidadRepository localidadRepository;
    private final ProvinciaRepository provinciaRepository;

    private static final String BASE_PATH = "c:/Users/denis/Desktop/IEI/";
    private static final Map<String, String> FUENTE_PATHS = new HashMap<>();

    static {
        FUENTE_PATHS.put("CV", BASE_PATH + "Comunitat Valenciana/estaciones.json");
        FUENTE_PATHS.put("CAT", BASE_PATH + "Catalunya/ITV-CAT.xml");
        FUENTE_PATHS.put("GAL", BASE_PATH + "Galicia/Estacions_ITV.csv");
    }

    public CargaService(WrapperJSON wrapperJSON, WrapperXML wrapperXML, WrapperCSV wrapperCSV,
            EstacionRepository estacionRepository,
            LocalidadRepository localidadRepository,
            ProvinciaRepository provinciaRepository) {
        this.wrapperJSON = wrapperJSON;
        this.wrapperXML = wrapperXML;
        this.wrapperCSV = wrapperCSV;
        this.estacionRepository = estacionRepository;
        this.localidadRepository = localidadRepository;
        this.provinciaRepository = provinciaRepository;
    }

    @Transactional
    public ResultadoCargaDTO cargarDesdeFuentes(List<String> fuentes) {
        ResultadoCargaDTO resultado = new ResultadoCargaDTO();

        for (String fuente : fuentes) {
            try {
                cargarDesdeFuente(fuente, resultado);
            } catch (Exception e) {
                // Log error but continue with other sources
                System.err.println("Error cargando fuente " + fuente + ": " + e.getMessage());
            }
        }

        return resultado;
    }

    private void cargarDesdeFuente(String fuente, ResultadoCargaDTO resultado) throws Exception {
        String filePath = FUENTE_PATHS.get(fuente.toUpperCase());
        if (filePath == null || !new File(filePath).exists()) {
            throw new RuntimeException("Archivo no encontrado para fuente: " + fuente);
        }

        JsonNode estacionesJson;

        switch (fuente.toUpperCase()) {
            case "CV":
                estacionesJson = wrapperJSON.convertirAJSON(filePath, resultado);
                break;
            case "CAT":
                estacionesJson = wrapperXML.convertirXMLaJSON(filePath, resultado);
                break;
            case "GAL":
                JsonNode galData = wrapperCSV.convertirCSVaJSON(filePath, resultado);
                estacionesJson = galData.get("estaciones");
                break;
            default:
                throw new RuntimeException("Fuente desconocida: " + fuente);
        }

        if (estacionesJson != null && estacionesJson.isArray()) {
            procesarYGuardarEstaciones(estacionesJson, resultado, fuente);
        } else if (estacionesJson != null) {
            procesarYGuardarEstaciones(estacionesJson, resultado, fuente);
        }
    }

    // Método auxiliar (temporal para reemplazo)
    private void procesarYGuardarEstaciones(JsonNode estacionesJson, ResultadoCargaDTO resultado, String fuente) {
        for (JsonNode estacionNode : estacionesJson) {
            try {
                Estacion estacion = crearEstacionDesdeJson(estacionNode);

                // Validación básica antes de guardar
                if (estacion.getNombre() == null || estacion.getNombre().isEmpty()) {
                    throw new RuntimeException("Nombre vacío");
                }

                // Detectar duplicados
                // TODO: Mejorar detección de duplicados

                estacionRepository.save(estacion);
                resultado.incrementarRegistrosCorrectos();

            } catch (Exception e) {
                String nombre = estacionNode.has("nombre") ? estacionNode.get("nombre").asText() : "Desconocido";
                String localidad = estacionNode.has("localidad_nombre") ? estacionNode.get("localidad_nombre").asText()
                        : "Desconocida";

                resultado.agregarErrorRechazado(new es.upv.iei.itv.dto.ErrorCargaDTO(
                        fuente,
                        nombre,
                        localidad,
                        "Error al guardar: " + e.getMessage(),
                        "Rechazado"));
            }
        }
    }

    private Estacion crearEstacionDesdeJson(JsonNode node) {
        Estacion estacion = new Estacion();

        estacion.setNombre(node.has("nombre") ? node.get("nombre").asText() : null);
        estacion.setDireccion(node.has("direccion") ? node.get("direccion").asText() : null);
        estacion.setCodigoPostal(node.has("codigo_postal") && !node.get("codigo_postal").isNull()
                ? node.get("codigo_postal").asText()
                : null);
        estacion.setDescripcion(node.has("descripcion") ? node.get("descripcion").asText() : null);
        estacion.setHorario(node.has("horario") ? node.get("horario").asText() : null);
        estacion.setContacto(node.has("contacto") ? node.get("contacto").asText() : null);
        estacion.setUrl(node.has("URL") ? node.get("URL").asText() : null);

        if (node.has("latitud") && !node.get("latitud").isNull()) {
            estacion.setLatitud(node.get("latitud").asDouble());
        }
        if (node.has("longitud") && !node.get("longitud").isNull()) {
            estacion.setLongitud(node.get("longitud").asDouble());
        }

        // Tipo
        if (node.has("tipo")) {
            String tipoStr = node.get("tipo").asText();
            try {
                estacion.setTipo(Tipo.valueOf(tipoStr));
            } catch (IllegalArgumentException e) {
                estacion.setTipo(Tipo.Otros);
            }
        }

        // Provincia
        String provinciaNombre = node.has("provincia_nombre") ? node.get("provincia_nombre").asText() : "Desconocida";
        String provinciaCodigo = node.has("provincia_codigo") ? node.get("provincia_codigo").asText() : "00";
        Provincia provincia = obtenerOCrearProvincia(provinciaNombre, provinciaCodigo);

        // Localidad
        String localidadNombre = node.has("localidad_nombre") ? node.get("localidad_nombre").asText() : "Desconocida";
        Localidad localidad = obtenerOCrearLocalidad(localidadNombre, provincia);

        estacion.setLocalidad(localidad);

        return estacion;
    }

    private Provincia obtenerOCrearProvincia(String nombre, String codigo) {
        return provinciaRepository.findByNombre(nombre)
                .orElseGet(() -> {
                    Provincia nueva = new Provincia(nombre);
                    return provinciaRepository.save(nueva);
                });
    }

    private Localidad obtenerOCrearLocalidad(String nombre, Provincia provincia) {
        return localidadRepository.findByNombreAndProvincia_Nombre(nombre, provincia.getNombre())
                .orElseGet(() -> {
                    Localidad nueva = new Localidad(nombre, provincia);
                    return localidadRepository.save(nueva);
                });
    }

    @Transactional
    public void eliminarTodosDatos() {
        estacionRepository.deleteAll();
        localidadRepository.deleteAll();
        provinciaRepository.deleteAll();
    }
}
