package es.upv.iei.api_busqueda.service;

import es.upv.iei.api_busqueda.model.Estacion;
import es.upv.iei.api_busqueda.model.Tipo;
import es.upv.iei.api_busqueda.repository.EstacionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstacionService {

    private final EstacionRepository estacionRepository;

    public EstacionService(EstacionRepository estacionRepository) {
        this.estacionRepository = estacionRepository;
    }

    public List<Estacion> buscarPorFiltros(String localidad, String codigoPostal, String provincia, String tipoStr) {
        Tipo tipo = null;
        if (tipoStr != null && !tipoStr.isEmpty()) {
            try {
                tipo = Tipo.valueOf(tipoStr);
            } catch (IllegalArgumentException e) {
                // Ignorar tipo inválido
            }
        }
        return estacionRepository.buscarPorFiltros(localidad, codigoPostal, provincia, tipo);
    }
}
