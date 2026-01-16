package es.upv.iei.api_carga.extractor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

@Component
public class ExtractorJSON {

    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String user;
    @Value("${spring.datasource.password}")
    private String password;

    private final Map<String, Long> provinciaCache = new HashMap<>();
    private final Map<String, Long> localidadCache = new HashMap<>();


    public void insertar(JsonNode estacionesArray) throws Exception {
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            conn.setAutoCommit(false);

            for (JsonNode estacion : estacionesArray) {
                String provinciaNombre = safeText(estacion.get("provincia_nombre"));
                String provinciaCodigoKey = safeText(estacion.get("provincia_codigo"));

                long provinciaId;
                if (provinciaCache.containsKey(provinciaCodigoKey)) {
                    provinciaId = provinciaCache.get(provinciaCodigoKey);
                } else {
                    provinciaId = insertarProvincia(conn, provinciaNombre, provinciaCodigoKey);
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

    private boolean estacionValida(JsonNode e) {

        String tipo = safeText(e.get("tipo"));

        if ("Estación móvil".equals(tipo) || "Estación agrícola".equals(tipo)) {

            if (!isEmpty(e, "direccion")) return false;
            if (!isEmpty(e, "codigo_postal")) return false;
            if (!isEmpty(e, "localidad_nombre")) return false;
            if (!isEmpty(e, "provincia_nombre")) return false;
            if (!isEmpty(e, "latitud")) return false;
            if (!isEmpty(e, "longitud")) return false;

            String contacto = safeText(e.get("contacto"));
            if (contacto == null || !contacto.contains("@")) return false;

            return true;
        }

        if ("Estación fija".equals(tipo)) {

            if (isEmpty(e, "nombre")) return false;
            if (isEmpty(e, "direccion")) return false;
            if (isEmpty(e, "codigo_postal")) return false;
            if (isEmpty(e, "localidad_nombre")) return false;
            if (isEmpty(e, "provincia_nombre")) return false;

            String horario = safeText(e.get("horario"));
            if (!horarioValido(horario)) return false;

            String cp = safeText(e.get("codigo_postal"));
            if (!cp.matches("\\d{5}")) return false;

            String prefijo = cp.substring(0, 2);
            if (!prefijo.matches("03|12|46")) return false;

            if (!isNumber(e, "latitud")) return false;
            if (!isNumber(e, "longitud")) return false;

            String contacto = safeText(e.get("contacto"));
            if (contacto == null || !contacto.contains("@")) return false;

            return true;
        }

        return false;
    }

    private boolean horarioValido(String h) {
        if (h == null) return false;
        return h.matches(".*([01]?\\d|2[0-3]):[0-5]\\d.*");
    }

    private boolean isEmpty(JsonNode e, String f) {
        return !e.has(f) || e.get(f).isNull() || e.get(f).asText().trim().isEmpty();
    }

    private boolean isNumber(JsonNode e, String f) {
        return e.has(f) && e.get(f).isNumber();
    }

    private String safeText(JsonNode node) {
        return (node == null || node.isNull()) ? null : node.asText().trim();
    }

    private long insertarProvincia(Connection conn, String nombre, String codigoKey) throws SQLException {
        String sql = "INSERT INTO provincia(nombre) VALUES (?) RETURNING codigo";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                long id = rs.getLong("codigo");
                provinciaCache.put(codigoKey, id);
                return id;
            }
        }
        return 0;
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