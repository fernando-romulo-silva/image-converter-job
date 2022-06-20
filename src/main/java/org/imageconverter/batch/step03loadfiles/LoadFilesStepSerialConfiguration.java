package org.imageconverter.batch.step03loadfiles;

import static java.io.File.separator;
import static org.imageconverter.config.BatchConfiguration.LOAD_FILE_STEP_SERIAL;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import org.imageconverter.infra.ImageFileLoad;
import org.slf4j.Logger;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
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

    @Bean
    @StepScope
    public FlatFileItemReader<ImageFileLoad> serialItemReader( //
		    //
		    @Value("#{jobParameters['fileName']}") //
		    final String fileName, //
		    //
		    @Value("${application.batch-folders.processing-files}") //
		    final Resource processingFolder, //
		    //
		    final LoadFileSetMapper loadFileSetMapper, //
		    //
		    final AbstractLineTokenizer imageFileDelimitedTokenizer

    ) throws IOException, URISyntaxException {

	LOGGER.info("Serial Reader");

	final var processingAbsolutePath = Paths.get(processingFolder.getURI());
	final var pathFile = Paths.get(processingAbsolutePath.toString() + separator + fileName);
	final var uri = new URI("file:" + pathFile.toString());

	final var lineMapper = new DefaultLineMapper<ImageFileLoad>();
	lineMapper.setLineTokenizer(imageFileDelimitedTokenizer);
	lineMapper.setFieldSetMapper(loadFileSetMapper);

	return new FlatFileItemReaderBuilder<ImageFileLoad>() //
			.name("serialItemReader") //
			.lineMapper(lineMapper) //
			.resource(new UrlResource(uri)) //
			.build();
    }

}
