package es.upv.iei.api_carga.controller;

import es.upv.iei.api_carga.dto.CargaRequestDTO;
import es.upv.iei.api_carga.dto.ResultadoCargaDTO;
import es.upv.iei.api_carga.service.CargaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la carga de datos ITV
 */
@RestController
@RequestMapping("/api/carga")
@CrossOrigin(origins = "http://localhost:9002")
public class CargaController {

    private final CargaService cargaService;

    public CargaController(CargaService cargaService) {
        this.cargaService = cargaService;
    }

    /**
     * Cargar datos desde fuentes especificadas
     */
    @PostMapping("/cargar")
    public ResponseEntity<ResultadoCargaDTO> cargar(@RequestBody CargaRequestDTO request) {
        ResultadoCargaDTO resultado = cargaService.cargarDesdeFuentes(request.getFuentes());
        return ResponseEntity.ok(resultado);
    }

    /**
     * Borrar todos los datos de la base de datos
     */
    @DeleteMapping("/borrar")
    public ResponseEntity<String> borrarTodos() {
        cargaService.eliminarTodosDatos();
        return ResponseEntity.ok("Datos eliminados correctamente");
    }
}
