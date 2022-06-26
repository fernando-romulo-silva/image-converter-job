package org.imageconverter.batch.step03loadfiles.serial;

import static org.imageconverter.config.BatchConfiguration.LOAD_FILE_STEP_SERIAL;
import static org.slf4j.LoggerFactory.getLogger;

import org.imageconverter.infra.ImageFileLoad;
import org.slf4j.Logger;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class LoadFilesStepSerialConfiguration {

    private static final Logger LOGGER = getLogger(LoadFilesStepSerialConfiguration.class);

    private final StepBuilderFactory stepBuilderFactory;

    @Autowired
    LoadFilesStepSerialConfiguration(final StepBuilderFactory stepBuilderFactory) {
	super();
	this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public Step loadFilesStepSerial( //
		    final ItemReader<ImageFileLoad> serialItemReader, //

//		    final ItemProcessor<ImageFileLoad, ImageFileLoad> itemProcessor, //
//		    final ItemWriter<ImageFileLoad> itemWriter, //

		    final PlatformTransactionManager transactionManager) {

	return this.stepBuilderFactory //
			.get(LOAD_FILE_STEP_SERIAL) //
			//
			.transactionManager(transactionManager) //
			.<ImageFileLoad, ImageFileLoad>chunk(1000) //
			//
			.reader(serialItemReader) //
			// .processor(item -> item) //
			.writer(items -> items.forEach(System.out::println)) //

			.build();
    }
}
