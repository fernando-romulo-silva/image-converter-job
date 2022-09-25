package org.imageconverter.batch.step03loadfile.serial;

import static org.imageconverter.config.BatchConfiguration.LOAD_FILE_STEP_SERIAL;

import org.imageconverter.domain.Image;
import org.imageconverter.infra.ImageFileLoad;
import org.imageconverter.util.DefaultStepListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class LoadFilesStepSerialConfiguration {

    private final StepBuilderFactory stepBuilderFactory;

    LoadFilesStepSerialConfiguration(final StepBuilderFactory stepBuilderFactory) {
	super();
	this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    Step loadFilesStepSerial( //
		    final ItemReader<ImageFileLoad> serialItemReader, //
		    final ItemProcessor<ImageFileLoad, Image> loadFileProcessor, //
		    final ItemWriter<Image> loadFileWriter, //
		    //
		    @Value("${application.chunk-size}") //
		    final Integer chunkSize,//
		    final DefaultStepListener defaultStepListener,
		    final PlatformTransactionManager transactionManager) {

	return this.stepBuilderFactory //
			.get(LOAD_FILE_STEP_SERIAL) //
			.listener(defaultStepListener) //
			//
			.transactionManager(transactionManager) //
			.<ImageFileLoad, Image>chunk(chunkSize) //
			//
			.reader(serialItemReader) //
			.processor(loadFileProcessor) //
			.writer(loadFileWriter) //
			//
			.build();
    }
}
