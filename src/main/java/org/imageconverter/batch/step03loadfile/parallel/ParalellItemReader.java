package org.imageconverter.batch.step03loadfile.parallel;

import java.net.MalformedURLException;

import org.imageconverter.batch.step03loadfile.LoadFileSetMapper;
import org.imageconverter.infra.ImageFileLoad;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.AbstractLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;

@StepScope
@Component
public class ParalellItemReader extends FlatFileItemReader<ImageFileLoad> {
    
    @Autowired
    ParalellItemReader( //
		    @Value("#{stepExecutionContext['fileName']}") // 
		    final String fileName, //
		    
		    final LoadFileSetMapper loadFileSetMapper, //
		    
		    final AbstractLineTokenizer imageFileDelimitedTokenizer) throws MalformedURLException {
	super();
	
	final var lineMapper = new DefaultLineMapper<ImageFileLoad>();
	lineMapper.setLineTokenizer(imageFileDelimitedTokenizer);
	lineMapper.setFieldSetMapper(loadFileSetMapper);
	
	setName("paralellItemReader"); 
	setResource(new UrlResource(fileName));
	setLineMapper(lineMapper);
    }
}
