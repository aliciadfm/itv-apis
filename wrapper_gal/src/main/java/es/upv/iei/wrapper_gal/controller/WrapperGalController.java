package es.upv.iei.wrapper_gal.controller;

import es.upv.iei.wrapper_gal.service.WrapperGalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import tools.jackson.databind.JsonNode;

public class WrapperGalController {
    private final WrapperGalService wrapperGalService;

    public WrapperGalController(WrapperGalService wrapperGalService) {
        this.wrapperGalService = wrapperGalService;
    }

    @GetMapping
    public ResponseEntity<JsonNode> getWrapperGal() throws Exception {
        return ResponseEntity.ok(wrapperGalService.obtenerEstaciones());
    }
}
