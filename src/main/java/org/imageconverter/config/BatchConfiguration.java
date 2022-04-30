package org.imageconverter.config;

import javax.sql.DataSource;

import org.imageconverter.infra.BatchSkipPolicy;
import org.imageconverter.util.RecordSepartatorPolicy;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.BatchConfigurationException;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.file.separator.SimpleRecordSeparatorPolicy;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

//    @Autowired
//    private StepBuilderFactory stepBuilderFactory;
//
//    @Autowired
//    private JpaTransactionManager jpaTransactionManager;

    private final DataSource batchDataSource;
    
    

    @Autowired //
    BatchConfiguration(
		    //
		    final JobBuilderFactory jobBuilderFactory,
		    //
		    @Qualifier("batchDataSource") //
		    final DataSource batchDataSource) {
	super();
	this.jobBuilderFactory = jobBuilderFactory;
	this.batchDataSource = batchDataSource;
    }

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
    // Job Configuration
    // -----------------------------------------------------------------------------------

    @Bean
    public Job job( //
		    final Step moveFileStep //
//		    final Step splitFileStep, //
//		    final Step loadFilesStep, //
//		    final Step convertionStep, //
//		    final Step deleteSplitedStep, //
//		    final Step finalizeStep

    ) {

	return jobBuilderFactory.get("convertImageJob") //
			.incrementer(new RunIdIncrementer()) //
			.start(moveFileStep) //
//			.next(splitFileStep) //
//			.next(loadFilesStep) //
//			.next(convertionStep) //
//			.next(deleteSplitedStep) //
//			.next(finalizeStep) //
			.build();
    }

//    @Bean
//    public JobParameters getJobParameters() {
//	final var jobParametersBuilder = new JobParametersBuilder();
//	    
//	jobParametersBuilder.addString("fileName", <dest_from_cmd_line);
////	jobParametersBuilder.addDate("date", <date_from_cmd_line>);
//	return jobParametersBuilder.toJobParameters();
//    }

    @Bean
    public JobRepository getJobRepository(
		    @Qualifier("batchPlatformTransactionManager")
		    final PlatformTransactionManager batchPlatformTransactionManager
		    ) {

	final var factoryBean = new JobRepositoryFactoryBean();

	factoryBean.setDataSource(batchDataSource);
	factoryBean.setTransactionManager(batchPlatformTransactionManager);
	factoryBean.setTablePrefix("BATCH_");
	factoryBean.setIsolationLevelForCreate("PROPAGATION_REQUIRED");

	try {

	    factoryBean.afterPropertiesSet();
	    return factoryBean.getObject();
	} catch (final Exception ex) {
	    throw new BatchConfigurationException(ex);
	}
    }

    @Bean
    public JobLauncher createJobLauncher(final JobRepository jobRepository) throws Exception {
	SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
	jobLauncher.setJobRepository(jobRepository);
	jobLauncher.afterPropertiesSet();
	return jobLauncher;
    }

    @Bean(name = "batchPlatformTransactionManager")
//    @Primary
    public PlatformTransactionManager batchPlatformTransactionManager() {
	return new DataSourceTransactionManager(batchDataSource);
    }

    @Bean
    public BatchConfigurer configurer() {
	return new DefaultBatchConfigurer(batchDataSource);
    }
}
