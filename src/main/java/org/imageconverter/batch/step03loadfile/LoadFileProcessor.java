package org.imageconverter.batch.step03loadfile;

import org.imageconverter.domain.BatchProcessingFile;
import org.imageconverter.domain.BatchProcessingFileRepository;
import org.imageconverter.domain.Image;
import org.imageconverter.infra.ImageFileLoad;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@StepScope
@Component
public class LoadFileProcessor implements ItemProcessor<ImageFileLoad, Image> {

    private final BatchProcessingFile batchProcessingFile;

    @Autowired
    public LoadFileProcessor( //

		    final BatchProcessingFileRepository batchProcessingFileRepository, //

		    @Value("#{jobParameters['fileName']}") //
		    final String fileName //
    ) {
	this.batchProcessingFile = batchProcessingFileRepository.findByName(fileName);
    }

    @Override
    public Image process(final ImageFileLoad item) throws Exception {

	final var image = new Image(item.fileName(), batchProcessingFile);

	return image;
    }

}
