package org.imageconverter.config;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppProperties {

    @Bean
    @ConfigurationProperties(prefix = "spring.jpa")
    public Map<String, String> domainJpaMap() {
	return new LinkedHashMap<>();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public Map<String, String> domainDataSourceMap() {
	return new LinkedHashMap<>();
    }
}
