package org.imageconverter;

import static org.springframework.boot.SpringApplication.run;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application's starter, just a main class
 * 
 * @author Fernando Romulo da Silva
 */
@SpringBootApplication
public class ImageJobApplication {

    /**
     * Main method.
     * 
     * @param args the application arguments
     */
    public static void main(String[] args) {
	run(ImageJobApplication.class, args);
    }
}
