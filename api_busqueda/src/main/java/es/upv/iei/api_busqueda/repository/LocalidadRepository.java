package es.upv.iei.api_busqueda.repository;

import es.upv.iei.api_busqueda.model.Localidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocalidadRepository extends JpaRepository<Localidad, Long> {
    Optional<Localidad> findByNombreAndProvincia_Nombre(String nombre, String provinciaNombre);
}
