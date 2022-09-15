package org.imageconverter.config.openfeign;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.codec.Decoder;
import feign.codec.StringDecoder;


@Configuration
@EnableFeignClients(basePackages = "org.imageconverter.util.http")
public class OpenFeignConfiguration extends AbstractOpenFeignConfiguration {

//    @Bean
//    RequestInterceptor requestInterceptor() {
//      return requestTemplate -> {
//          requestTemplate.header("Accept", ContentType.APPLICATION_JSON.getMimeType());
//      };
//    }
    
    @Bean
    Decoder feignDecoder() {
	return new StringDecoder();
    }
}
