package org.imageconverter.batch.step04conversion;

import org.imageconverter.domain.Image;
import org.imageconverter.util.http.ConvertImageClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@StepScope
@Component
public class ConversionItemProcessor implements ItemProcessor<Image, Image> {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    
    private final ConvertImageClient convertImageClient;
    
    ConversionItemProcessor(final ConvertImageClient convertImageClient) {
	super();
	this.convertImageClient = convertImageClient;
    }

    public Image process(final Image item) throws Exception {
	
	LOGGER.info("Item {} is processing", item);
	
	return item;
    }

}
