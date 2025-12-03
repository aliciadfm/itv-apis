package es.upv.iei.wrapper_gal;

import es.upv.iei.wrapper_gal.extractor.ExtractorGal;
import es.upv.iei.wrapper_gal.wrapper.WrapperGal;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tools.jackson.databind.JsonNode;

@SpringBootApplication
public class WrapperGalApplication {

	public static void main(String[] args) throws Exception {
		//SpringApplication.run(WrapperGalApplication.class, args);
        WrapperGal wrapperGal = new WrapperGal();
        ExtractorGal  extractorGal = new ExtractorGal();

        JsonNode jsonNode = wrapperGal.convertirCSVaJSON("src/main/resources/Estacions_ITV.csv");
        extractorGal.insertar(jsonNode);
	}

}
