package gm.zona_fit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ZonaFitApplication {

    private static final Logger logger =
            LoggerFactory.getLogger(ZonaFitApplication.class);

    public static void main(String[] args) {
        logger.info("Iniciando la aplicacion");
        SpringApplication.run(ZonaFitApplication.class, args);
        logger.info("Aplicacion finalizada!");
    }
}
