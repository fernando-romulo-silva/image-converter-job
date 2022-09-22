package org.imageconverter.util.http;

import static org.imageconverter.config.ImageConverterServiceConst.ACTUATOR_HEALTH_URL;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import org.imageconverter.config.openfeign.OpenFeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(//
		value = "actuator", //
		url = "${application.image-converter-service.url}", //
		configuration = OpenFeignConfiguration.class //
)
public interface ActuatorServiceClient {

    @GetMapping(value = ACTUATOR_HEALTH_URL, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<String> checkStatus(); 
}
