package es.upv.iei.wrapper_cat.extractor;

import org.springframework.beans.factory.annotation.Value;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ExtractorCat {
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String user;
    @Value("${spring.datasource.password}")
    private String password;

    private final Map<String, Long> provinciaCache = new HashMap<>();
    private final Map<String, Long> localidadCache = new HashMap<>();

    public void insertar(JsonNode jsonMultiEntidad) throws Exception {
        url = "jdbc:postgresql://localhost:5432/postgres";
        user = "postgres";
        password = "contrasenya";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            conn.setAutoCommit(false);

            ArrayNode provincias = (ArrayNode) jsonMultiEntidad.get("provincias");
            for (JsonNode provincia : provincias) {
                String nombre = limpiar(provincia.get("nombre").asText(), "Desconocida");
                String codigo = limpiar(provincia.get("codigo").asText(), "00");
                long id = getOrInsertProvincia(conn, nombre, codigo);
                provinciaCache.put(nombre, id);
            }

            ArrayNode localidades = (ArrayNode) jsonMultiEntidad.get("localidades");
            for (JsonNode localidad : localidades) {
                String nombre = limpiar(localidad.get("nombre").asText(), "Desconocido");
                String codigo = limpiar(localidad.get("codigo").asText(), "00");
                String provinciaNombre = limpiar(localidad.get("provincia_nombre").asText(), "Desconocida");
                long provinciaId = provinciaCache.getOrDefault(provinciaNombre, getOrInsertProvincia(conn, provinciaNombre, codigo));
                long id = getOrInsertLocalidad(conn, nombre, provinciaId, codigo);
                localidadCache.put(nombre + "_" + provinciaId, id);
            }

            ArrayNode estaciones = (ArrayNode) jsonMultiEntidad.get("estaciones");
            for (JsonNode estacion : estaciones) {
                String nombreEstacion = limpiar(estacion.get("nombre").asText(), "Desconocida");
                String localidadNombre = limpiar(estacion.get("localidad_nombre").asText(), "Desconocido");
                String localidadCodigo = limpiar(estacion.get("localidad_codigo").asText(), "00");
                String provinciaNombre = limpiar(estacion.get("provincia_nombre").asText(), "Desconocida");
                long provinciaId = provinciaCache.getOrDefault(provinciaNombre, getOrInsertProvincia(conn, provinciaNombre, estacion.get("provincia_codigo").asText()));
                long localidadId = localidadCache.getOrDefault(localidadNombre + "_" + provinciaId, getOrInsertLocalidad(conn, localidadNombre, provinciaId, localidadCodigo));

                if (!estacionExiste(conn, nombreEstacion, localidadId)) {
                    insertarEstacion(conn, estacion, localidadId);
                }
            }

            conn.commit();
            System.out.println("Inserción Multi-Entidad CSV completada correctamente.");
        }
    }

    private String limpiar(String str, String siNulo) {
        return (str == null || str.trim().isEmpty()) ? siNulo : str.trim();
    }

    private long getOrInsertProvincia(Connection conn, String nombre, String codigo) throws SQLException {
        if (provinciaCache.containsKey(nombre)) return provinciaCache.get(nombre);
        String selectSql = "SELECT codigo FROM provincia WHERE nombre = ?";
        try (PreparedStatement stmt = conn.prepareStatement(selectSql)) {
            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                long id = rs.getLong("codigo");
                provinciaCache.put(nombre, id);
                return id;
            }
        }
        String insertSql = "INSERT INTO provincia(nombre) VALUES (?) RETURNING codigo";
        try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                long id = rs.getLong("codigo");
                provinciaCache.put(nombre, id);
                return id;
            }
        }
        throw new SQLException("Error insertando Provincia: " + nombre);
    }

    private long getOrInsertLocalidad(Connection conn, String nombre, long provinciaId, String codigo) throws SQLException {
        String key = nombre + "_" + provinciaId;
        if (localidadCache.containsKey(key)) return localidadCache.get(key);
        String selectSql = "SELECT codigo FROM localidad WHERE nombre = ? AND provincia_codigo = ?";
        try (PreparedStatement stmt = conn.prepareStatement(selectSql)) {
            stmt.setString(1, nombre);
            stmt.setLong(2, provinciaId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                long id = rs.getLong("codigo");
                localidadCache.put(key, id);
                return id;
            }
        }
        String insertSql = "INSERT INTO localidad(nombre, provincia_codigo) VALUES (?, ?) RETURNING codigo";
        try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
            stmt.setString(1, nombre);
            stmt.setLong(2, provinciaId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                long id = rs.getLong("codigo");
                localidadCache.put(key, id);
                return id;
            }
        }
        throw new SQLException("Error insertando Localidad: " + nombre);
    }

    private boolean estacionExiste(Connection conn, String nombreEstacion, long localidadId) throws SQLException {
        String sql = "SELECT cod_estacion FROM estacion WHERE nombre = ? AND localidad_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombreEstacion);
            stmt.setLong(2, localidadId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    private void insertarEstacion(Connection conn, JsonNode estacion, long localidadId) throws SQLException {
        String sql = """
            INSERT INTO estacion(
                nombre, tipo, direccion, codigo_postal,
                longitud, latitud, descripcion, horario,
                contacto, url, localidad_id
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, estacion.get("nombre").asText());
            stmt.setString(2, estacion.get("tipo").asText());
            stmt.setString(3, estacion.get("direccion").asText());
            stmt.setString(4, estacion.get("codigo_postal").asText());
            setNullableDouble(stmt, 5, estacion.get("longitud"));
            setNullableDouble(stmt, 6, estacion.get("latitud"));
            stmt.setString(7, estacion.get("descripcion").asText());
            stmt.setString(8, estacion.get("horario").asText());
            stmt.setString(9, estacion.get("contacto").asText());
            stmt.setString(10, estacion.get("URL").asText());
            stmt.setLong(11, localidadId);

            stmt.executeUpdate();
        }
    }

    private void setNullableDouble(PreparedStatement stmt, int idx, JsonNode node) throws SQLException {
        if (node == null || node.isNull()) {
            stmt.setNull(idx, Types.DOUBLE);
        } else {
            try {
                stmt.setDouble(idx, node.asDouble());
            } catch (Exception e) {
                stmt.setNull(idx, Types.DOUBLE);
            }
        }
    }
}
