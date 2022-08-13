package org.imageconverter.util.http;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import org.imageconverter.config.OpenFeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(//
		value = "jplaceholder", //
		url = "https://jsonplaceholder.typicode.com/", //
		configuration = OpenFeignConfiguration.class //
)
public interface ConvertImageClient {

    @PostMapping(consumes = { MULTIPART_FORM_DATA_VALUE }, produces = APPLICATION_JSON_VALUE)
    ImageConverterPostResponse convert(
		    @RequestParam(name = "file", required = true) //
		    final ImageConverterRequest request);
}
