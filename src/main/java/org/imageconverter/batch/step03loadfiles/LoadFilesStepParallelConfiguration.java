package org.imageconverter.batch.step03loadfiles;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.net.MalformedURLException;

import org.imageconverter.domain.ImageFileLoad;
import org.slf4j.Logger;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.AbstractLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class LoadFilesStepParallelConfiguration {

    private static final Logger LOGGER = getLogger(LoadFilesStepParallelConfiguration.class);

    private final StepBuilderFactory stepBuilderFactory;

    @Autowired
    LoadFilesStepParallelConfiguration(final StepBuilderFactory stepBuilderFactory) {
	super();
	this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public Step loadFilesStepParalell( //
		    final Step loadFilesStepParalellSlave, //
		    final Partitioner partitioner, //
		    final ThreadPoolTaskExecutor taskExecutor //
    ) {

	return this.stepBuilderFactory //
			.get("loadFilesStepParalell") //
			.partitioner("loadFilesStepParalellSlave", partitioner) //
			.step(loadFilesStepParalellSlave) //
			.taskExecutor(taskExecutor) //
			.build();
    }

    @Bean
    public Step loadFilesStepParalellSlave( //
		    final ItemReader<ImageFileLoad> paralellItemReader, //
		    
		    //final ItemProcessor<String, ImageFileLoad> itemProcessor, //
		    //final ItemWriter<ImageFileLoad> itemWriter, //
		    
		    final PlatformTransactionManager transactionManager) {

	return this.stepBuilderFactory //
			.get("loadFilesStepParalellSlave") //
			//
			.transactionManager(transactionManager) //
			.<ImageFileLoad, ImageFileLoad>chunk(1000) //
			//
			.reader(paralellItemReader) //
			//.processor(itemProcessor) //
			.writer(items -> items.forEach(System.out::println)) //
			.build();
    }  

    @Bean
    @StepScope
    public FlatFileItemReader<ImageFileLoad> paralellItemReader( //
		    //
		    @Value("#{stepExecutionContext['fileName']}") //
		    final String fileName, //

		    final LoadFileSetMapper loadFileSetMapper, //

		    final AbstractLineTokenizer imageFileTokenizer

    ) throws MalformedURLException {

	LOGGER.info("Parallel Reader");

	final var lineMapper = new DefaultLineMapper<ImageFileLoad>();
	lineMapper.setLineTokenizer(imageFileTokenizer);
	lineMapper.setFieldSetMapper(loadFileSetMapper);

	return new FlatFileItemReaderBuilder<ImageFileLoad>() //
			.name("paralellItemReader") //
			.lineMapper(lineMapper) //
			.resource(new UrlResource(fileName)) //
			.build();
    }    
    
    
    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
	final var taskExecutor = new ThreadPoolTaskExecutor();
	taskExecutor.setThreadGroupName("parallelThreadPoolTaskExecutor");
	taskExecutor.setMaxPoolSize(10);
	taskExecutor.setCorePoolSize(10);
	taskExecutor.setQueueCapacity(10);
	taskExecutor.afterPropertiesSet();
	return taskExecutor;
    }

    @Bean
    @StepScope
    public Partitioner partitioner() {
    	
	LOGGER.info("In Partitioner");
    	
	final var partitioner = new MultiResourcePartitioner();
	final var resolver = new PathMatchingResourcePatternResolver();

	Resource[] resources = null;
	try {
	    resources = resolver.getResources("/*.txt");
	} catch (IOException e) {
	    e.printStackTrace();
	}

	partitioner.setResources(resources);
	partitioner.partition(10);
	return partitioner;
    }
}
