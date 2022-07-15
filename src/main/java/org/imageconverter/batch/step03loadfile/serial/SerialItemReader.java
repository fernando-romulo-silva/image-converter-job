package org.imageconverter.batch.step03loadfile.serial;

import static java.io.File.separator;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import org.imageconverter.batch.step03loadfile.LoadFileSetMapper;
import org.imageconverter.infra.ImageFileLoad;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.AbstractLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;

@StepScope
@Component
public class SerialItemReader extends FlatFileItemReader<ImageFileLoad> {

    @Autowired
    SerialItemReader( //
		    @Value("#{jobParameters['fileName']}") //
		    final String fileName, //
		    //
		    @Value("${application.batch-folders.processing-files}") //
		    final Resource processingFolder, //
		    //
		    final LoadFileSetMapper loadFileSetMapper, //
		    //
		    final AbstractLineTokenizer imageFileDelimitedTokenizer //

    ) throws IOException, URISyntaxException {

	final var processingAbsolutePath = Paths.get(processingFolder.getURI());
	final var pathFile = Paths.get(processingAbsolutePath.toString() + separator + fileName);
	final var uri = new URI("file:" + pathFile.toString());

	final var lineMapper = new DefaultLineMapper<ImageFileLoad>();
	lineMapper.setFieldSetMapper(loadFileSetMapper);
	lineMapper.setLineTokenizer(imageFileDelimitedTokenizer);

	setName("serialItemReader");
	setResource(new UrlResource(uri));
	setLineMapper(lineMapper);
    }
}
