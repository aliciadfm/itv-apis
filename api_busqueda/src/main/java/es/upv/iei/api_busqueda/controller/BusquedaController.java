package es.upv.iei.api_busqueda.controller;

import es.upv.iei.api_busqueda.dto.BusquedaRequestDTO;
import es.upv.iei.api_busqueda.dto.EstacionDTO;
import es.upv.iei.api_busqueda.mapper.EstacionMapper;
import es.upv.iei.api_busqueda.service.EstacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/busqueda")
@CrossOrigin(origins = "http://localhost:9011")
public class BusquedaController {

    private final EstacionService estacionService;

    public BusquedaController(EstacionService estacionService) {
        this.estacionService = estacionService;
    }

    @PostMapping("/estaciones")
    public ResponseEntity<List<EstacionDTO>> buscar(@RequestBody BusquedaRequestDTO request) {
        List<EstacionDTO> estaciones = estacionService.buscarPorFiltros(
                request.getLocalidad(),
                request.getCodigoPostal(),
                request.getProvincia(),
                request.getTipo()).stream()
                .map(EstacionMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(estaciones);
    }
}
