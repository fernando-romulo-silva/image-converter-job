package org.imageconverter.util.http;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response to post (create) convert image operation.
 * 
 * @author Fernando Romulo da Silva
 */
public record ImageConverterPostResponse( //
		
		@JsonProperty(value = "text", required = true) //
		String text) {
}