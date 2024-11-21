package org.imageconverter;

import org.springframework.boot.SpringApplication;
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
    public static void main(final String[] args) {
        SpringApplication.run(ImageJobApplication.class, args);
    }
}
