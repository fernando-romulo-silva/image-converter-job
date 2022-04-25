package org.imageconverter.config;

import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class PersistenceJpaConfig {

    private final DataSource domainDataSource;

    private final Map<String, String> domainJpaMap;

    @Autowired
    public PersistenceJpaConfig(final DataSource domainDataSource, final Map<String, String> domainJpaMap) {
	super();
	this.domainDataSource = domainDataSource;
	this.domainJpaMap = domainJpaMap;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
	final var emf = new LocalContainerEntityManagerFactoryBean();
	emf.setDataSource(domainDataSource);
	emf.setPackagesToScan("org.imageconverter");

	final var jpaProperties = new Properties();
	jpaProperties.putAll(domainJpaMap);

	final var vendorAdapter = new HibernateJpaVendorAdapter();
	emf.setJpaVendorAdapter(vendorAdapter);
	emf.setJpaProperties(jpaProperties);

	return emf;
    }

    @Bean
    @Primary
    public JpaTransactionManager jpaTransactionManger() {
	final var tm = new JpaTransactionManager();
	tm.setDataSource(domainDataSource);
	return tm;
    }
}
