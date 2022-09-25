package org.imageconverter.batch.step05conversion;

import static org.imageconverter.config.BatchConfiguration.CONVERTION_STEP;

import java.net.SocketTimeoutException;

import org.imageconverter.domain.Image;
import org.imageconverter.util.DefaultStepListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ConvertionStepConfiguration {

    private final StepBuilderFactory stepBuilderFactory;

    ConvertionStepConfiguration(final StepBuilderFactory stepBuilderFactory) {
	super();
	this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    Step convertionStep( //
		    final ItemReader<Image> conversionItemReader, //
		    final ItemProcessor<Image, Image> conversionItemProcessor, //
		    final ItemWriter<Image> convertionItemWriter, //
		    //
		    @Value("${application.chunk-size}") //
		    final Integer chunkSize, //
		    final DefaultStepListener defaultStepListener, //
		    final PlatformTransactionManager transactionManager) {

	return this.stepBuilderFactory //
			.get(CONVERTION_STEP) //
			.listener(defaultStepListener) //
			//
			.transactionManager(transactionManager) //
			.<Image, Image>chunk(chunkSize) //
			//
			.reader(conversionItemReader) //
			.processor(conversionItemProcessor) //
			.writer(convertionItemWriter) //
			//
			.faultTolerant() //
			.retryLimit(3) //
			//.retry(ConnectTimeoutException.class)
			.retry(SocketTimeoutException.class) //
			.retry(DeadlockLoserDataAccessException.class)
			//
			.build();
    }
}
