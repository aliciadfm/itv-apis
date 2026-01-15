package es.upv.iei.api_carga.repository;

import es.upv.iei.api_carga.model.Localidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocalidadRepository extends JpaRepository<Localidad, Long> {
    Optional<Localidad> findByNombreAndProvincia_Nombre(String nombre, String provinciaNombre);
}
