package org.imageconverter.batch.step03loadfiles;

import static org.slf4j.LoggerFactory.getLogger;

import java.net.MalformedURLException;

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
			.get("loadFilesStepSerial") //
			//
			.transactionManager(transactionManager) //
			.<ImageFileLoad, ImageFileLoad>chunk(1000) //
			//
			.reader(serialItemReader) //
			//.processor(item -> item) //
			.writer(items -> items.forEach(System.out::println)) //

			.build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<ImageFileLoad> serialItemReader( //
		    //
		    @Value("#{jobParameters['fileName']}") //
		    final String fileName, //

		    final LoadFileSetMapper loadFileSetMapper, //

		    final AbstractLineTokenizer imageFileTokenizer

    ) throws MalformedURLException {

	LOGGER.info("Parallel Reader");

	final var lineMapper = new DefaultLineMapper<ImageFileLoad>();
	lineMapper.setLineTokenizer(imageFileTokenizer);
	lineMapper.setFieldSetMapper(loadFileSetMapper);

	return new FlatFileItemReaderBuilder<ImageFileLoad>() //
			.name("serialItemReader") //
			.lineMapper(lineMapper) //
			.resource(new UrlResource(fileName)) //
			.build();
    }

}
