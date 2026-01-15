package es.upv.iei.api_carga.service;

import es.upv.iei.api_carga.model.Localidad;
import es.upv.iei.api_carga.repository.LocalidadRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LocalidadService {

    private final LocalidadRepository localidadRepository;

    public LocalidadService(LocalidadRepository localidadRepository) {
        this.localidadRepository = localidadRepository;
    }

    public List<Localidad> obtenerTodos() {
        return localidadRepository.findAll();
    }

    public Optional<Localidad> obtenerPorId(Long id) {
        return localidadRepository.findById(id);
    }

    public Localidad crear(Localidad localidad) {
        return localidadRepository.save(localidad);
    }

    public void eliminar(Localidad localidad) {
        localidadRepository.delete(localidad);
    }

}
