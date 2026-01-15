package es.upv.iei.api_carga.service;


import es.upv.iei.api_carga.model.Provincia;
import es.upv.iei.api_carga.repository.ProvinciaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProvinciaService {

    private final ProvinciaRepository provinciaRepository;

    public ProvinciaService(ProvinciaRepository provinciaRepository) {
        this.provinciaRepository = provinciaRepository;
    }

    public List<Provincia> obtenerTodos() {
        return provinciaRepository.findAll();
    }

    public Optional<Provincia> obtenerPorId(Long id) {
        return provinciaRepository.findById(id);
    }

    public Provincia crear(Provincia provincia) {
        return provinciaRepository.save(provincia);
    }

    public void eliminar(Provincia provincia) {
        provinciaRepository.delete(provincia);
    }
}
