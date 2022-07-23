package org.imageconverter.config;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppProperties {

    @Bean(name = "domainJpaMap")
    @ConfigurationProperties(prefix = "spring.jpa")
    Map<String, String> domainJpaMap() {
	return new LinkedHashMap<>();
    }

    @Bean(name = "domainDataSourceMap")
    @ConfigurationProperties(prefix = "spring.datasource")
    Map<String, String> domainDataSourceMap() {
	return new LinkedHashMap<>();
    }
}
