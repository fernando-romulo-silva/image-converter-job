package org.imageconverter.config;

import static org.imageconverter.batch.step02splitfile.SplitFileStepExecutionDecider.FLOW_STATUS_CONTINUE_PARALELL;
import static org.imageconverter.batch.step02splitfile.SplitFileStepExecutionDecider.FLOW_STATUS_CONTINUE_SERIAL;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
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
    public static final String LOAD_FILE_STEP_PARALELL = "loadFilesStepParalell";
    public static final String LOAD_FILE_STEP_SERIAL = "loadFilesStepSerial";
    public static final String CONVERTION_STEP = "convertionStep";

    private final JobBuilderFactory jobBuilderFactory;

    private final DataSource batchDataSource;

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
    Job job( //
		    final Step moveFileStep, // Step 1
		    final Step splitFileStep, // Step 2
		    final Step loadFilesStepSerial, // Step 3.1
		    final Step loadFilesStepParalell, // Step 3.2
		    final Step convertionStep, // 4
//		    final Step deleteSplitedStep, //
//		    final Step finalizeStep,
		    final JobExecutionDecider splitFileStepExecutionDecider//
    ) {

	return jobBuilderFactory.get(CONVERT_IMAGE_JOB) //
			.incrementer(new RunIdIncrementer()) //
			.start(moveFileStep) // move file to processing's folder
			//
			.next(splitFileStepExecutionDecider) // check if it needs split the file
			/*--*/.from(splitFileStepExecutionDecider) //
			/*-------*/.on(FLOW_STATUS_CONTINUE_PARALELL) // Let split it 
			/*----------*/.to(splitFileStep) // split it!
			/*----------*/.next(loadFilesStepParalell) // load in paralell
			/*--*/.from(splitFileStepExecutionDecider) // 
			/*-------*/.on(FLOW_STATUS_CONTINUE_SERIAL)// We don't need split
			/*----------*/.to(loadFilesStepSerial) // load in serial
			//
			.from(moveFileStep) // back to root path
			.next(convertionStep)
			//			
//			.next(convertionStep) //
			//.next(deleteSplitedStep) //
			//.next(finalizeStep) //
			.end()
			.build();
    }

//    @Bean
//    JobParameters getJobParameters() {
//	final var jobParametersBuilder = new JobParametersBuilder();
//	    
//	jobParametersBuilder.addString("fileName", <dest_from_cmd_line);
//	jobParametersBuilder.addDate("date", <date_from_cmd_line>);
//	return jobParametersBuilder.toJobParameters();
//    }

    @Bean
    JobRepository getJobRepository( //
		    @Qualifier("batchPlatformTransactionManager") //
		    final PlatformTransactionManager batchPlatformTransactionManager) throws Exception {

	final var factoryBean = new JobRepositoryFactoryBean();

	factoryBean.setDataSource(batchDataSource);
	factoryBean.setTransactionManager(batchPlatformTransactionManager);
	factoryBean.setTablePrefix("BATCH_");
	factoryBean.setIsolationLevelForCreate("PROPAGATION_REQUIRED");

	factoryBean.afterPropertiesSet();
	return factoryBean.getObject();
    }

    @Bean
    JobLauncher createJobLauncher(final JobRepository jobRepository) throws Exception {
	SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
	jobLauncher.setJobRepository(jobRepository);
	jobLauncher.afterPropertiesSet();
	return jobLauncher;
    }

    @Bean(name = "batchPlatformTransactionManager")
//    @Primary
    PlatformTransactionManager batchPlatformTransactionManager() {
	return new DataSourceTransactionManager(batchDataSource);
    }

    @Bean
    BatchConfigurer configurer() {
	return new DefaultBatchConfigurer(batchDataSource);
    }
}
