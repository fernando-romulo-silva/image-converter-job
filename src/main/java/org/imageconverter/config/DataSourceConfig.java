package org.imageconverter.config;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.BatchDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DataSourceConfig {

    private final Map<String, String> domainDataSourceMap;

    @Autowired
    public DataSourceConfig(final Map<String, String> domainDataSourceMap) {
	super();
	this.domainDataSourceMap = domainDataSourceMap;
    }

    @Bean(name = "batchDataSource")
    @BatchDataSource
    public DataSource batchDataSource() {
	final var batchDataSource = new EmbeddedDatabaseBuilder() //
			.setType(EmbeddedDatabaseType.HSQL) //
			.setName("batchDataSource") //
			.addScripts(//
					"classpath:org/springframework/batch/core/schema-drop-hsqldb.sql", //
					"classpath:org/springframework/batch/core/schema-hsqldb.sql") //
			.build();

	return batchDataSource;
    }

    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSource domainDataSource(final DataSourceProperties properties) {

	final var dataSource = properties.initializeDataSourceBuilder() //
			.type(HikariDataSource.class) //
			.build(); //

	dataSource.setPoolName("domainDataSource");

	dataSource.setDriverClassName(domainDataSourceMap.get("driver-class-name"));
	dataSource.setJdbcUrl(domainDataSourceMap.get("url"));
	dataSource.setUsername(domainDataSourceMap.get("username"));
	dataSource.setPassword(domainDataSourceMap.get("password"));

	return dataSource;
    }

}
