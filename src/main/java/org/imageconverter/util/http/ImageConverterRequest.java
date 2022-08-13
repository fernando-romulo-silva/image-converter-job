package org.imageconverter.util.http;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Structure to execute the image convertion.
 * 
 * @author Fernando Romulo da Silva
 */
public record ImageConverterRequest( //
		
		@NotEmpty(message = "The 'fileName' cannot be empty") //
		String fileName, //
		//
		@NotNull(message = "The 'fileContent' cannot be null") //
		byte[] fileContent, //
		//
		@NotNull(message = "The 'executionType' cannot be null") //
		String executionType) {
}
