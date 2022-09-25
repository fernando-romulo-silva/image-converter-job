package org.imageconverter.config.openfeign;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.client.RestTemplate;

import feign.auth.BasicAuthRequestInterceptor;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import feign.jackson.JacksonDecoder;

@Configuration
@EnableFeignClients(basePackages = "org.imageconverter.util.http")
public class OpenFeignSecurityConfiguration extends AbstractOpenFeignConfiguration {

    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;

    @Bean
    Decoder feignDecoder() {
	return new JacksonDecoder();
    }

//    @Bean
//    Encoder multipartFormEncoder() {
//        return new SpringFormEncoder(new SpringEncoder(new ObjectFactory<HttpMessageConverters>() {
//            @Override
//            public HttpMessageConverters getObject() throws BeansException {
//                return new HttpMessageConverters(new RestTemplate().getMessageConverters());
//            }
//        }));
//    }

    @Bean
    @Lazy
    Encoder multipartFormEncoder() {
	return new SpringFormEncoder(new SpringEncoder(messageConverters));
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
