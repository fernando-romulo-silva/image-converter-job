package org.imageconverter.config;

import javax.sql.DataSource;

import org.imageconverter.infra.BatchSkipPolicy;
import org.imageconverter.util.RecordSepartatorPolicy;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.BatchConfigurationException;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.file.separator.SimpleRecordSeparatorPolicy;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Value("${application.file-input}")
    private Resource file;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private JpaTransactionManager jpaTransactionManager;

    @Autowired
    private DataSource batchDataSource;
    
    // -----------------------------------------------------------------------------------
    // Util
    // -----------------------------------------------------------------------------------

    @Bean
    public SkipPolicy fileVerificationSkipper() {
	return new BatchSkipPolicy();
    }

    @Bean
    public SimpleRecordSeparatorPolicy blankLineRecordSeparatorPolicy() {
	return new RecordSepartatorPolicy();
    }

    @Bean
    public FixedLengthTokenizer fixedLengthTokenizer() {
	final var tokenizer = new FixedLengthTokenizer();

	tokenizer.setNames( //
			"id", //
			"fileName", //
			"image" //
	);

	tokenizer.setColumns( //
			new Range(1, 10), //
			new Range(12, 22), //
			new Range(24, 1000) //
	);

	tokenizer.setStrict(false);

	return tokenizer;
    }

    // -----------------------------------------------------------------------------------
    // Step StepLoadFile
    // -----------------------------------------------------------------------------------

    // -----------------------------------------------------------------------------------
    // Step StepProcessFile
    // -----------------------------------------------------------------------------------
    
    // -----------------------------------------------------------------------------------
    // Step StepFinalizeFile
    // -----------------------------------------------------------------------------------    
    
    // -----------------------------------------------------------------------------------
    // Job Configuration
    // -----------------------------------------------------------------------------------

    @Bean
    public Job job( //
		    final Step stepLoadFile, //
		    final Step stepProcessFile, //
		    final Step stepFinalizeFile) {

	return jobBuilderFactory.get("convertImageJob") //
			.incrementer(new RunIdIncrementer()) //
			.start(stepLoadFile) //
			.next(stepProcessFile) //
			.next(stepFinalizeFile) //
			.build();
    }

    @Bean
    public JobRepository getJobRepository() {

	final var factoryBean = new JobRepositoryFactoryBean();

	factoryBean.setDataSource(batchDataSource);
	factoryBean.setTransactionManager(new DataSourceTransactionManager(batchDataSource));
	factoryBean.setTablePrefix("BATCH_");
	factoryBean.setIsolationLevelForCreate("PROPAGATION_REQUIRED");

	try {

	    factoryBean.afterPropertiesSet();
	    return factoryBean.getObject();
	} catch (Exception ex) {
	    throw new BatchConfigurationException(ex);
	}
    }

}
