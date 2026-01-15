package es.upv.iei.wrapper_cat.controller;

import es.upv.iei.wrapper_cat.service.WrapperCatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import tools.jackson.databind.JsonNode;

public class WrapperCatController {
    private final WrapperCatService wrapperCatService;

    public WrapperCatController(WrapperCatService wrapperCatService) {
        this.wrapperCatService = wrapperCatService;
    }

    @GetMapping
    public ResponseEntity<JsonNode> getWrapperGal() throws Exception {
        return ResponseEntity.ok(wrapperCatService.obtenerEstaciones());
    }
}
