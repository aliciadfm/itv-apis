package es.upv.iei.wrapper_cat;

import es.upv.iei.wrapper_cat.extractor.ExtractorCat;
import es.upv.iei.wrapper_cat.wrapper.WrapperCat;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tools.jackson.databind.JsonNode;

import java.io.IOException;

@SpringBootApplication
public class WrapperCatApplication {

	public static void main(String[] args) throws Exception {
		//SpringApplication.run(WrapperCatApplication.class, args);
        WrapperCat wrapperCat = new WrapperCat();
        ExtractorCat  extractorCat = new ExtractorCat();

        JsonNode jsonCat = wrapperCat.convertirCSVaJSON("src/main/resources/ITV-CAT.xml");
        extractorCat.insertar(jsonCat);
	}

}
