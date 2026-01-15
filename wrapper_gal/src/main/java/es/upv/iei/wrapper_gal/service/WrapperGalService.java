package es.upv.iei.wrapper_gal.service;

import es.upv.iei.wrapper_gal.wrapper.WrapperGal;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

@Service
public class WrapperGalService {
    private final WrapperGal wrapperGal;

    public WrapperGalService(WrapperGal wrapperGal) {
        this.wrapperGal = wrapperGal;
    }

    public JsonNode obtenerEstaciones() throws Exception {
        return wrapperGal.convertirCSVaJSON("src/main/resources/Estacions_ITV.csv");
    }
}
