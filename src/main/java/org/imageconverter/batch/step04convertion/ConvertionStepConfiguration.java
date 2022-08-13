package org.imageconverter.batch.step04convertion;

import static org.imageconverter.config.BatchConfiguration.CONVERTION_STEP;

import org.imageconverter.domain.Image;
import org.imageconverter.util.http.ConvertImageClient;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ConvertionStepConfiguration {
    
    private final StepBuilderFactory stepBuilderFactory;
    
    private final ConvertImageClient convertImageClient;
    
    ConvertionStepConfiguration(final StepBuilderFactory stepBuilderFactory, final ConvertImageClient convertImageClient) {
	super();
	this.stepBuilderFactory = stepBuilderFactory;
	this.convertImageClient = convertImageClient;
    }
    
    @Bean
    Step convertionStep( //
//		    final ItemReader<Image> convertionItemReader, //
//		    final ItemProcessor<Image, Image> convertionProcessor, //
//		    final ItemWriter<Image> convertionWriter, //

		    final PlatformTransactionManager transactionManager) {

	return this.stepBuilderFactory //
			.get(CONVERTION_STEP) //
			//
			.transactionManager(transactionManager) //
			.<Image, Image>chunk(1000) //
			//
//			.reader(convertionItemReader) //
//			.processor(convertionProcessor) //
//			.writer(convertionWriter) //
			//
			.reader(() -> new Image("",null)) //
//			.processor(i -> j) //
			.writer(i -> System.out.println()) //
			//
			.build();
			
			
    }
}
