package org.imageconverter.util.http;

import static org.imageconverter.config.ImageConverterServiceConst.ACTUATOR_HEALTH_URL;
import static org.imageconverter.config.ImageConverterServiceConst.CONVERTION_URL;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

import org.imageconverter.config.OpenFeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(//
		value = "image-converter-service", //
		url = "${application.image-converter-service.url}", //
		configuration = OpenFeignConfiguration.class //
)
public interface ConvertImageServiceClient {

    @PostMapping(value = CONVERTION_URL, consumes = { MULTIPART_FORM_DATA_VALUE }, produces = APPLICATION_JSON_VALUE)
    ImageConverterPostResponse convert(
		    @RequestParam(name = "file", required = true) //
		    final ImageConverterRequest request);
    
    
    @GetMapping(value = ACTUATOR_HEALTH_URL, produces = APPLICATION_JSON_VALUE)
    String checkStatus(); 
}
