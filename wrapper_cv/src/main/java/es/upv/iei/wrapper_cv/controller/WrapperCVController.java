package es.upv.iei.wrapper_cv.controller;

import es.upv.iei.wrapper_cv.service.WrapperCVService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import tools.jackson.databind.JsonNode;

public class WrapperCVController {
    private final WrapperCVService wrapperCVService;

    public WrapperCVController(WrapperCVService wrapperCVService){
        this.wrapperCVService = wrapperCVService;
    }

    @GetMapping
    public ResponseEntity<JsonNode> getWrapperCV() throws Exception {
        return ResponseEntity.ok(wrapperCVService.obtenerEstaciones());
    }
}
