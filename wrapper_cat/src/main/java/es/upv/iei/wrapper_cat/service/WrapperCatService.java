package es.upv.iei.wrapper_cat.service;

import es.upv.iei.wrapper_cat.wrapper.WrapperCat;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

@Service
public class WrapperCatService {
    private final WrapperCat wrapperCat;

    public WrapperCatService(WrapperCat wrapperCat) {
        this.wrapperCat = wrapperCat;
    }

    public JsonNode obtenerEstaciones() throws Exception {
        return wrapperCat.convertirXMLaJSON("src/main/resources/estaciones.json");
    }
}
