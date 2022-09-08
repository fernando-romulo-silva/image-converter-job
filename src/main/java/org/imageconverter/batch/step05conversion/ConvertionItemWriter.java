package org.imageconverter.batch.step05conversion;

import java.util.List;

import org.imageconverter.domain.Image;
import org.imageconverter.domain.ImageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@StepScope
@Component
public class ConvertionItemWriter implements ItemWriter<Image> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConvertionItemWriter.class);

    private final ImageRepository imageRepository;

    ConvertionItemWriter(final ImageRepository imageRepository) {
	super();
	this.imageRepository = imageRepository;
    }

    @Override
    public void write(final List<? extends Image> list) throws Exception {

	LOGGER.info("Save '{}' images", list.size());

	imageRepository.saveAll(list);
    }
}
