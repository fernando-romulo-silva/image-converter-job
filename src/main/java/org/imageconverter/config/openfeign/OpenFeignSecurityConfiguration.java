package org.imageconverter.config.openfeign;

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
    Decoder feignDecoder2() {
	return new JacksonDecoder();
    }

    @Bean
    BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
	return new BasicAuthRequestInterceptor("username", "password");
    }
}
