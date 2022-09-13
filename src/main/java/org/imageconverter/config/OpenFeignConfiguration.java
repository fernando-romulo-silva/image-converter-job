package org.imageconverter.config;

import java.io.IOException;

import org.imageconverter.infra.exception.BaseApplicationException;
import org.imageconverter.infra.exception.ConversionAlreadyExistsException;
import org.imageconverter.infra.exception.ConversionErrorException;
import org.imageconverter.infra.exception.ConversionServiceUnavaillableException;
import org.imageconverter.infra.exception.UnexpectedConversionException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Response;
import feign.codec.Decoder;
import feign.codec.ErrorDecoder;
import feign.codec.StringDecoder;
import feign.jackson.JacksonDecoder;


@Configuration
@EnableFeignClients(basePackages = "org.imageconverter.util.http")
public class OpenFeignConfiguration {

//    @Bean
//    RequestInterceptor requestInterceptor() {
//      return requestTemplate -> {
//          requestTemplate.header("Accept", ContentType.APPLICATION_JSON.getMimeType());
//      };
//    }
    
//    @Bean
//    BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
//        return new BasicAuthRequestInterceptor("username", "password");
//    }
    
    @Bean
    Decoder feignDecoder() {
	return new StringDecoder();
    }
    
//    @Bean
//    Decoder feignDecoder2() {
//      return new JacksonDecoder();
//    }
    
    
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
    
//    @Bean
//    Logger.Level feignLoggerLevel() {
//        return Logger.Level.HEADERS;
//    }
}
