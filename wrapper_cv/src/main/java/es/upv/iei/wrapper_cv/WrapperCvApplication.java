package es.upv.iei.wrapper_cv;

import es.upv.iei.wrapper_cv.extractor.ExtractorCV;
import es.upv.iei.wrapper_cv.wrapper.WrapperCV;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tools.jackson.databind.JsonNode;

@SpringBootApplication
public class WrapperCvApplication {

	public static void main(String[] args) throws Exception {
		//SpringApplication.run(WrapperCvApplication.class, args);
        WrapperCV wrapperCV = new WrapperCV();
        ExtractorCV extractorCV = new ExtractorCV();

        JsonNode jsonCV = wrapperCV.convertirAJSON("src/main/resources/estaciones.json");
        extractorCV.insertar(jsonCV);
	}
}
