package es.upv.iei.api_carga.extractor;

import com.fasterxml.jackson.databind.JsonNode;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ExtractorXML {

    private final String url = "jdbc:postgresql://localhost:5432/postgres";
    private final String user = "postgres";
    private final String password = "contrasenya";

    private final Map<String, Long> provinciaCache = new HashMap<>();
    private final Map<String, Long> localidadCache = new HashMap<>();

    public void insertar(JsonNode estacionesArray) throws Exception {

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            conn.setAutoCommit(false);

            for (JsonNode estacion : estacionesArray) {

                String provinciaNombre = estacion.get("provincia_nombre").asText();
                String provinciaCodigoKey = estacion.get("provincia_codigo").asText();
                long provinciaId = provinciaCache.containsKey(provinciaCodigoKey)
                        ? provinciaCache.get(provinciaCodigoKey)
                        : getOrInsertProvincia(conn, provinciaNombre, provinciaCodigoKey);

                String localidadNombre = estacion.get("localidad_nombre").asText();
                String localidadKey = localidadNombre + "_" + provinciaId;
                long localidadId = localidadCache.containsKey(localidadKey)
                        ? localidadCache.get(localidadKey)
                        : getOrInsertLocalidad(conn, localidadNombre, provinciaId, localidadKey);

                if (!estacionExiste(conn, estacion.get("nombre").asText(), localidadId)) {
                    insertarEstacion(conn, estacion, localidadId);
                }
            }
            conn.commit();
            System.out.println("Inserción Multi-Entidad XML completada correctamente.");
        }
    }

    private long getOrInsertProvincia(Connection conn, String nombre, String codigoKey) throws SQLException {

        String selectSql = "SELECT codigo FROM provincia WHERE nombre = ?";
        try (PreparedStatement stmt = conn.prepareStatement(selectSql)) {
            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                long id = rs.getLong("codigo");
                provinciaCache.put(codigoKey, id);
                return id;
            }
        }

        String insertSql = """
            INSERT INTO provincia(nombre)
            VALUES (?)
            RETURNING codigo
        """;
        try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                long id = rs.getLong("codigo");
                provinciaCache.put(codigoKey, id);
                return id;
            }
        }
        throw new SQLException("Error insertando Provincia");
    }

    private long getOrInsertLocalidad(Connection conn, String nombre, long provinciaId, String localidadKey) throws SQLException {
        String selectSql = "SELECT codigo FROM localidad WHERE nombre = ? AND provincia_codigo = ?";
        try (PreparedStatement stmt = conn.prepareStatement(selectSql)) {
            stmt.setString(1, nombre);
            stmt.setLong(2, provinciaId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                long id = rs.getLong("codigo");
                localidadCache.put(localidadKey, id);
                return id;
            }
        }
        String insertSql = """
            INSERT INTO localidad(nombre, provincia_codigo)
            VALUES (?, ?)
            RETURNING codigo
        """;
        try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
            stmt.setString(1, nombre);
            stmt.setLong(2, provinciaId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                long id = rs.getLong("codigo");
                localidadCache.put(localidadKey, id);
                return id;
            }
        }
        throw new SQLException("Error insertando Localidad");
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
                nombre, tipo, direccion,
                codigo_postal, longitud, latitud,
                descripcion, horario, contacto, url,
                localidad_id
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
