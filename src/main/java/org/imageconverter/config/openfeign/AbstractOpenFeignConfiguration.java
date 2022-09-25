package org.imageconverter.config.openfeign;

import static org.imageconverter.config.ImageConverterServiceConst.ACTUATOR_HEALTH_URL;

import java.io.IOException;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.imageconverter.infra.exception.BaseApplicationException;
import org.imageconverter.infra.exception.ConversionAlreadyExistsException;
import org.imageconverter.infra.exception.ConversionErrorException;
import org.imageconverter.infra.exception.ConversionServiceUnavaillableException;
import org.imageconverter.infra.exception.ServerHealthDownException;
import org.imageconverter.infra.exception.UnexpectedConversionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import feign.Response;
import feign.codec.ErrorDecoder;

public abstract class AbstractOpenFeignConfiguration {

    @Bean
    ErrorDecoder errorDecode(@Value("${application.image-converter-service.url}") final String serverURL) {
	return (final String methodKey, final Response response) -> {

	    String msg;
	    try {
		
		if (Objects.nonNull(response.body())) {
		    msg = new String(response.body().asInputStream().readAllBytes());
		} else {
		    msg = StringUtils.EMPTY;
		}
		
	    } catch (IOException e) {
		msg = StringUtils.EMPTY;
	    }

	    switch (response.status()) {
	    case 400:
		return new ConversionErrorException(msg);
	    case 404:
		return new ConversionServiceUnavaillableException();
	    case 409:
		return new ConversionAlreadyExistsException();
	    case 500:
		return new UnexpectedConversionException(msg);
	    case 503:
		return new ServerHealthDownException(serverURL + ACTUATOR_HEALTH_URL);

	    default:
		return new BaseApplicationException("Generic error: " + msg);
	    }
	};
    }

    // methodKey ActuatorServiceClient#checkStatus()

//  @Bean
//  Logger.Level feignLoggerLevel() {
//      return Logger.Level.HEADERS;
//  }
}
