package org.imageconverter.batch.step04conversion;

import static org.imageconverter.config.BatchConfiguration.CONVERTION_STEP;

import org.imageconverter.domain.Image;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

		    final PlatformTransactionManager transactionManager) {

	return this.stepBuilderFactory //
			.get(CONVERTION_STEP) //
			//
			.transactionManager(transactionManager) //
			.<Image, Image>chunk(1000) //
			//
			.reader(conversionItemReader) //
			.processor(conversionItemProcessor) //
			.writer(convertionItemWriter) //
			//
			.build();
    }
}
