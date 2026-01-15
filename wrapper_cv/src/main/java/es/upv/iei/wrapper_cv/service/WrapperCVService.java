package es.upv.iei.wrapper_cv.service;

import es.upv.iei.wrapper_cv.wrapper.WrapperCV;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

@Service
public class WrapperCVService {
    private final WrapperCV wrapperCV;

    public WrapperCVService(WrapperCV wrapperCV) {
        this.wrapperCV = wrapperCV;
    }

    public JsonNode obtenerEstaciones() throws Exception {
        return wrapperCV.convertirAJSON("src/main/resources/Estacions_ITV.csv");
    }
}
