package org.imageconverter.batch.step03loadfile;

import org.imageconverter.domain.Image;
import org.imageconverter.infra.ImageFileLoad;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@StepScope
@Component
public class LoadFileProcessor implements ItemProcessor<ImageFileLoad, Image> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemProcessor.class);
    
    private final String fileName;

    LoadFileProcessor(@Value("#{jobParameters['fileName']}") final String fileName) {
	this.fileName = fileName;
    }

    @Override
    public Image process(final ImageFileLoad item) throws Exception {

	LOGGER.info("");
	
	final var image = new Image(item.fileName(), fileName);

	return image;
    }
}
