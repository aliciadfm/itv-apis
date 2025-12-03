package es.upv.iei.wrapper_cv.extractor;

import org.springframework.beans.factory.annotation.Value;
import tools.jackson.databind.JsonNode;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ExtractorCV {

    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String user;
    @Value("${spring.datasource.password}")
    private String password;

    private final Map<String, Long> provinciaCache = new HashMap<>();
    private final Map<String, Long> localidadCache = new HashMap<>();

    public void insertar(JsonNode estacionesArray) throws Exception {
        url = "jdbc:postgresql://localhost:5432/postgres";
        user = "postgres";
        password = "contrasenya";
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            conn.setAutoCommit(false);

            for (JsonNode estacion : estacionesArray) {
                String provinciaNombre = estacion.get("provincia_nombre").asText();
                String provinciaCodigoKey = estacion.get("provincia_codigo").asText();

                long provinciaId;
                if (provinciaCache.containsKey(provinciaCodigoKey)) {
                    provinciaId = provinciaCache.get(provinciaCodigoKey);
                } else {
                    provinciaId = insertarProvincia(conn, provinciaNombre, Long.getLong(provinciaCodigoKey));
                }

                String localidadNombre = estacion.get("localidad_nombre").asText();
                String localidadKey = localidadNombre + "_" + provinciaId;

                long localidadId;
                if (localidadCache.containsKey(localidadKey)) {
                    localidadId = localidadCache.get(localidadKey);
                } else {
                    localidadId = insertarLocalidad(conn, localidadNombre, provinciaId, localidadKey);
                }

                insertarEstacion(conn, estacion, localidadId);
            }

            conn.commit();
            System.out.println("Inserción completada correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private long insertarProvincia(Connection conn, String nombre, long codigo) throws SQLException {

        String sql = "INSERT INTO provincia(codigo, nombre) VALUES (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, codigo);
            stmt.setString(2, nombre);
            stmt.executeUpdate();

            provinciaCache.put(String.valueOf(codigo), codigo);
            return codigo;
        }
    }

    private long insertarLocalidad(Connection conn, String nombre, long provinciaId, String key) throws SQLException {
        String sql = "INSERT INTO localidad(nombre, provincia_codigo) VALUES (?, ?) RETURNING codigo";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
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
        if (node == null || node.isNull() || !node.isNumber()) {
            stmt.setNull(idx, Types.DOUBLE);
        } else {
            stmt.setDouble(idx, node.asDouble());
        }
    }
}
