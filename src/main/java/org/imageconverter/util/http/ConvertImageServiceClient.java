package org.imageconverter.util.http;

import static org.imageconverter.config.ImageConverterServiceConst.CONVERTION_URL;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

import org.imageconverter.config.openfeign.OpenFeignSecurityConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(//
		value = "converter", //
		url = "${application.image-converter-service.url}", //
		configuration = OpenFeignSecurityConfiguration.class //
)
public interface ConvertImageServiceClient {

    @PostMapping(value = CONVERTION_URL, consumes = { MULTIPART_FORM_DATA_VALUE }, produces = APPLICATION_JSON_VALUE)
    ImageConverterPostResponse convert(
		    @RequestParam(name = "file", required = true) //
		    final ImageConverterRequest request);
}
