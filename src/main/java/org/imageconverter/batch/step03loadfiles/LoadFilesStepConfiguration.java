package org.imageconverter.batch.step03loadfiles;

import java.io.IOException;

import org.imageconverter.domain.ImageFileLoad;
import org.imageconverter.infra.BatchSkipPolicy;
import org.imageconverter.util.RecordSepartatorPolicy;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.separator.SimpleRecordSeparatorPolicy;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

//@Configuration
public class LoadFilesStepConfiguration {

    private final StepBuilderFactory stepBuilderFactory;

//    @Autowired
    LoadFilesStepConfiguration(final StepBuilderFactory stepBuilderFactory) {
	super();
	this.stepBuilderFactory = stepBuilderFactory;
    }

//    @Bean
    public Step loadFilesStep( //
		    final ItemReader<String> itemReader, //
		    final ItemProcessor<String, ImageFileLoad> itemProcessor, //
		    final ItemWriter<ImageFileLoad> itemWriter, //
		    final PlatformTransactionManager transactionManager) {

	return this.stepBuilderFactory //
			.get("loadFilesStep") //
			//
			.transactionManager(transactionManager) //
			.<String, ImageFileLoad>chunk(1000) //
			//
			.reader(itemReader) //
			.processor(itemProcessor) //
			.writer(itemWriter) //
			.build();
    }
    
    
    public Step loadFilesStepParalell( //
		    final ItemReader<String> itemReader, //
		    final ItemProcessor<String, ImageFileLoad> itemProcessor, //
		    final ItemWriter<ImageFileLoad> itemWriter, //
		    final PlatformTransactionManager transactionManager) {

	return this.stepBuilderFactory //
			.get("loadFilesStep") //
			//
			.transactionManager(transactionManager) //
			.<String, ImageFileLoad>chunk(1000) //
			//
			.reader(itemReader) //
			.processor(itemProcessor) //
			.writer(itemWriter) //
			.build();
    }


    // -------------------------------------------------------------------------------------------------------------
    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
	final var taskExecutor = new ThreadPoolTaskExecutor();
	taskExecutor.setMaxPoolSize(10);
	taskExecutor.setCorePoolSize(10);
	taskExecutor.setQueueCapacity(10);
	taskExecutor.afterPropertiesSet();
	return taskExecutor;
    }

    @Bean("partitioner")
    @StepScope
    public Partitioner partitioner() {
//    	log.info("In Partitioner");
	final var partitioner = new MultiResourcePartitioner();
	final var resolver = new PathMatchingResourcePatternResolver();
	Resource[] resources = null;
	try {
	    resources = resolver.getResources("/*.csv");
	} catch (IOException e) {
	    e.printStackTrace();
	}
	partitioner.setResources(resources);
	partitioner.partition(10);
	return partitioner;
    }

    @Bean
    @Qualifier("masterStep")
    public Step masterStep() {
	return stepBuilderFactory.get("masterStep") //
			.partitioner("step1", partitioner()) //
//			.step(step1()) //
			.taskExecutor(taskExecutor()) //
			.build();
    }

//    @Bean
    public SkipPolicy fileVerificationSkipper() {
	return new BatchSkipPolicy();
    }

//    @Bean
    public SimpleRecordSeparatorPolicy blankLineRecordSeparatorPolicy() {
	return new RecordSepartatorPolicy();
    }

//    @Bean
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
}
