package es.upv.iei.api_carga.service;

import es.upv.iei.api_carga.dto.ResultadoCargaDTO;
import es.upv.iei.api_carga.extractor.ExtractorCSV;
import es.upv.iei.api_carga.extractor.ExtractorJSON;
import es.upv.iei.api_carga.extractor.ExtractorXML;
import es.upv.iei.api_carga.repository.EstacionRepository;
import es.upv.iei.api_carga.repository.LocalidadRepository;
import es.upv.iei.api_carga.repository.ProvinciaRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class CargaService {

    private final ExtractorJSON extractorJSON;
    private final ExtractorXML extractorXML;
    private final ExtractorCSV extractorCSV;
    private final EstacionRepository estacionRepository;
    private final LocalidadRepository localidadRepository;
    private final ProvinciaRepository provinciaRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${wrapper.cv.url:http://localhost:9003/convert}")
    private String wrapperCvUrl;

    @Value("${wrapper.cat.url:http://localhost:9004/convert}")
    private String wrapperCatUrl;

    @Value("${wrapper.gal.url:http://localhost:9005/convert}")
    private String wrapperGalUrl;

    public CargaService(ExtractorJSON extractorJSON,
            ExtractorXML extractorXML,
            ExtractorCSV extractorCSV,
            EstacionRepository estacionRepository,
            LocalidadRepository localidadRepository,
            ProvinciaRepository provinciaRepository) {
        this.extractorJSON = extractorJSON;
        this.extractorXML = extractorXML;
        this.extractorCSV = extractorCSV;
        this.estacionRepository = estacionRepository;
        this.localidadRepository = localidadRepository;
        this.provinciaRepository = provinciaRepository;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    @Transactional
    public ResultadoCargaDTO cargarDesdeFuentes(List<String> fuentes) {
        ResultadoCargaDTO resultado = new ResultadoCargaDTO();

        for (String fuente : fuentes) {
            try {
                cargarDesdeFuente(fuente, resultado);
            } catch (Exception e) {
                System.err.println("Error cargando fuente " + fuente + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        return resultado;
    }

    private void cargarDesdeFuente(String fuente, ResultadoCargaDTO resultado) throws Exception {
        JsonNode jsonData;

        switch (fuente.toUpperCase()) {

            case "CV":
                String cvResponse = restTemplate.getForObject(wrapperCvUrl, String.class);
                jsonData = objectMapper.readTree(cvResponse);
                extractorJSON.insertar(jsonData);
                break;

            case "CAT":
                String catResponse = restTemplate.getForObject(wrapperCatUrl, String.class);
                jsonData = objectMapper.readTree(catResponse);
                extractorJSON.insertar(jsonData);
                break;

            case "GAL":
                String galResponse = restTemplate.getForObject(wrapperGalUrl, String.class);
                jsonData = objectMapper.readTree(galResponse);
                extractorJSON.insertar(jsonData);
                break;

            default:
                throw new RuntimeException("Fuente desconocida: " + fuente);
        }

        resultado.incrementarRegistrosCorrectos();
    }

    @Transactional
    public void eliminarTodosDatos() {
        estacionRepository.deleteAll();
        localidadRepository.deleteAll();
        provinciaRepository.deleteAll();
    }
}
