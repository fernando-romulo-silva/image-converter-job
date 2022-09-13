package org.imageconverter.config;

/**
 * Image Converter URLs.
 * 
 * @author Fernando Romulo da Silva
 */
public final class ImageConverterServiceConst {


    public static final String CONVERTION_URL = "/rest/images/conversion";

    public static final String CONVERTION_AREA_URL = "/rest/images/conversion/area";
    
    public static final String ACTUATOR_HEALTH_URL = "/actuator/health";

    private ImageConverterServiceConst() {
	throw new IllegalStateException("You can't instanciate this class");
    }
}