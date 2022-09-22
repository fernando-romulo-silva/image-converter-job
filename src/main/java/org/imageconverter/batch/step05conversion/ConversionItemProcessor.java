package org.imageconverter.batch.step05conversion;

import java.util.Map;

import org.imageconverter.domain.Image;
import org.imageconverter.util.http.ConvertImageServiceClient;
import org.imageconverter.util.http.ImageConverterRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@StepScope
@Component
public class ConversionItemProcessor implements ItemProcessor<Image, Image> {
    
    private static final String EXECUTION_TYPE = "BATCH";

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private final ConvertImageServiceClient convertImageServiceClient;

    ConversionItemProcessor(final ConvertImageServiceClient convertImageServiceClient) {
	super();
	this.convertImageServiceClient = convertImageServiceClient;
    }

    public Image process(final Image item) throws Exception {

	logger.info("Item {} is processing", item);

	final var csr = "";

	final var headers = Map.<String, Object>of("X-CSRF-TOKEN", csr);
	
	final var request = new ImageConverterRequest(item.getName(), null, EXECUTION_TYPE);

	convertImageServiceClient.convert(headers, request);

	return item;
    }

}
