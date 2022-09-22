package org.imageconverter.config.openfeign;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.auth.BasicAuthRequestInterceptor;
import feign.codec.Decoder;
import feign.jackson.JacksonDecoder;

@Configuration
@EnableFeignClients(basePackages = "org.imageconverter.util.http")
public class OpenFeignSecurityConfiguration extends AbstractOpenFeignConfiguration {

    @Bean
    Decoder feignDecoder() {
	return new JacksonDecoder();
    }

    @Bean
    BasicAuthRequestInterceptor basicAuthRequestInterceptor( //
		    @Value("${application.image-converter-service.user}") //
		    final String user, //
		    //
		    @Value("${application.image-converter-service.password}") //
		    final String password) {
	return new BasicAuthRequestInterceptor(user, password);
    }
}
