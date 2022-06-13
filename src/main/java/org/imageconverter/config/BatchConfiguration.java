package org.imageconverter.config;

import static org.imageconverter.batch.step02splitfile.SplitFileStepExecutionDecider.*;

import javax.sql.DataSource;

import org.imageconverter.batch.step02splitfile.SplitFileStepExecutionDecider;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    public static final String CONVERT_IMAGE_JOB = "convertImageJob";
    public static final String MOVE_FILE_STEP = "moveFileStep";
    public static final String SPLIT_FILE_STEP = "splitFileStep";

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
    // Job Configuration
    // -----------------------------------------------------------------------------------

    @Bean
    public Job job( //
		    final Step moveFileStep, //
		    final Step splitFileStep, //
		    final Step loadFilesStepSerial, //
		    final Step loadFilesStepParalell, //
//		    final Step convertionStep, //
//		    final Step deleteSplitedStep, //
//		    final Step finalizeStep,
		    final SplitFileStepExecutionDecider splitFileStepExecutionDecider//
    ) {

	return jobBuilderFactory.get(CONVERT_IMAGE_JOB) //
			.incrementer(new RunIdIncrementer()) //
			.start(moveFileStep) //
			.next(splitFileStepExecutionDecider) //
			/*--*/.on(FLOW_STATUS_CONTINUE_PARALELL) //
			/*-------*/.to(splitFileStep) //
			/*-------*/.next(loadFilesStepParalell) //
			/*--*/.on(FLOW_STATUS_CONTINUE_SERIAL)//
			/*-------*/.to(loadFilesStepSerial) //
			.end() //
//			.fro
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
    public JobRepository getJobRepository( //
		    @Qualifier("batchPlatformTransactionManager") //
		    final PlatformTransactionManager batchPlatformTransactionManager) {

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
