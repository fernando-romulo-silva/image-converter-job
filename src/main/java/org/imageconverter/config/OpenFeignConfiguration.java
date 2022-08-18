package org.imageconverter.config;

import java.io.IOException;

import org.imageconverter.infra.exception.BaseApplicationException;
import org.imageconverter.infra.exception.ConversionAlreadyExistsException;
import org.imageconverter.infra.exception.ConversionErrorException;
import org.imageconverter.infra.exception.ConversionServiceUnavaillableException;
import org.imageconverter.infra.exception.UnexpectedConversionException;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Logger;
import feign.Response;
import feign.auth.BasicAuthRequestInterceptor;
import feign.codec.ErrorDecoder;


@Configuration
@EnableFeignClients
public class OpenFeignConfiguration {

//    @Bean
//    RequestInterceptor requestInterceptor() {
//      return requestTemplate -> {
//          requestTemplate.header("Accept", ContentType.APPLICATION_JSON.getMimeType());
//      };
//    }
    
    @Bean
    BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
        return new BasicAuthRequestInterceptor("username", "password");
    }
    
    @Bean
    ErrorDecoder errorDecode( ) {
	return (final String methodKey, final Response response) -> {
	  
	    	String msg;
		try {
		    msg = new String(response.body().asInputStream().readAllBytes());
		} catch (IOException e) {
		    msg = "";
		}
		
	        switch (response.status()){
	            case 400:
	                return new ConversionErrorException(msg);
	            case 404:
	                return new ConversionServiceUnavaillableException();
	            case 409:    
	        	return new ConversionAlreadyExistsException();
	            case 500:    
	        	return new UnexpectedConversionException(msg);	        	
	            default:
	                return new BaseApplicationException("Generic error");
	        }
	};
    }
    
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }
}
