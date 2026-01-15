package es.upv.iei.wrapper_cv.wrapper;

import es.upv.iei.wrapper_cv.service.CoordenadasService;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;

@Component
public class WrapperCV {
    private final ObjectMapper mapper = new ObjectMapper();
    private int estacionId = 1;

    public JsonNode convertirAJSON(String filePath) throws IOException {
        JsonNode rootNode = mapper.readTree(new File(filePath));

        ArrayNode estacionesArray = mapper.createArrayNode();

        if (rootNode.isArray()) {
            for (JsonNode raw : rootNode) {
                ObjectNode estacion = mapper.createObjectNode();

                String nombreRaw = getText(raw, "MUNICIPIO");
                String direccion = getText(raw, "DIRECCIÓN");
                String cp = getText(raw, "C.POSTAL");
                String provincia = getText(raw, "PROVINCIA");
                String tipoRaw = getText(raw, "TIPO ESTACIÓN");
                String numeroEstacion = getText(raw, "Nº ESTACIÓN");
                String horario = getText(raw, "HORARIOS");
                String correo = getText(raw, "CORREO");

                estacion.put("cod_estacion", estacionId++);

                String nombreFinal = "Estación ITV de " + (isNullOrEmpty(nombreRaw) ? "Desconocido" : nombreRaw);
                estacion.put("nombre", nombreFinal);

                String tipoFinal =  mapTipo(tipoRaw);
                estacion.put("tipo",tipoFinal);
                if(tipoFinal.equals("Estación fija")){
                    estacion.put("latitud", CoordenadasService.obtenerLatLon(direccion)[0]);
                    estacion.put("longitud", CoordenadasService.obtenerLatLon(direccion)[1]);
                    estacion.put("codigo_postal", isNullOrEmpty(cp) ? "0" : cp);
                } else {
                    estacion.putNull("latitud");
                    estacion.putNull("longitud");
                    estacion.putNull("codigo_postal");
                }

                estacion.put("direccion", direccion);// Fix CP vacío
                estacion.put("descripcion", direccion + " / " + horario);
                estacion.put("horario", horario);
                estacion.put("contacto", correo);
                estacion.put("URL", "www.sitval.com");

                estacion.put("localidad_nombre", isNullOrEmpty(nombreRaw) ? "Desconocido" : nombreRaw);
                estacion.put("provincia_nombre", isNullOrEmpty(provincia) ? "Desconocido" : provincia);

                String codProv;
                if (!isNullOrEmpty(cp) && cp.length() >= 2) {
                    codProv = cp.substring(0, 2);
                } else {
                    codProv = inferirProvincia(numeroEstacion);
                }
                estacion.put("provincia_codigo", codProv);

                if (!estacion.has("latitud") || !estacion.has("longitud")) {
                    estacion.putNull("latitud");
                    estacion.putNull("longitud");
                }

                estacionesArray.add(estacion);
            }
        }
        return estacionesArray;
    }

    private String getText(JsonNode node, String field) {
        return (node.has(field) && !node.get(field).isNull()) ? node.get(field).asText().trim() : "";
    }

    private boolean isNullOrEmpty(String s) { return s == null || s.trim().isEmpty(); }

    private String mapTipo(String tipo) {
        if (tipo == null) return "Otros";
        if (tipo.toLowerCase().contains("fija")) return "Estación fija";
        if (tipo.toLowerCase().contains("móvil")) return "Estación móvil";
        return "Otros";
    }

    private String inferirProvincia(String num) {
        String cod = inferirCodigoProvincia(num);
        System.out.println("Cod provincia: " + cod);
        return switch (cod) {
            case "12" -> "Castellón";
            case "03" -> "Alicante";
            case "46" -> "Valencia";
            default -> "Desconocido";
        };
    }

    private String inferirCodigoProvincia(String num) {
        if (num != null && num.length() >= 2) return num.substring(0, 2);
        return "00";
    }
}
