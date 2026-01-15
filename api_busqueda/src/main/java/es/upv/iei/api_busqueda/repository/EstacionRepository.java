package es.upv.iei.api_busqueda.repository;

import es.upv.iei.api_busqueda.model.Estacion;
import es.upv.iei.api_busqueda.model.Tipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstacionRepository extends JpaRepository<Estacion, Long> {

    @Query("SELECT e FROM Estacion e WHERE " +
            "(:localidad IS NULL OR LOWER(e.localidad.nombre) LIKE LOWER(CONCAT('%', :localidad, '%'))) AND " +
            "(:codigoPostal IS NULL OR e.codigoPostal = :codigoPostal) AND " +
            "(:provincia IS NULL OR LOWER(e.localidad.provincia.nombre) LIKE LOWER(CONCAT('%', :provincia, '%'))) AND "
            +
            "(:tipo IS NULL OR e.tipo = :tipo)")
    List<Estacion> buscarPorFiltros(
            @Param("localidad") String localidad,
            @Param("codigoPostal") String codigoPostal,
            @Param("provincia") String provincia,
            @Param("tipo") Tipo tipo);
}
