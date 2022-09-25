package org.imageconverter.batch.step02splitfile;

import static java.io.File.separator;

import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.SystemCommandTasklet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
@StepScope
public class SplitFileTasklet extends SystemCommandTasklet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SplitFileTasklet.class);

    private final String fileName;

    private final Resource processingFolder;

    private final Long splitFileSize;

    SplitFileTasklet(//
		    @Value("#{jobParameters['fileName']}") //
		    final String newFileName, //
		    //
		    @Value("${application.split-file-size}") // 
		    final Long newSplitFileSize,
		    //
		    @Value("${application.batch-folders.processing-files}") //
		    final Resource newProcessingFolder) throws IOException {
	super();

	this.fileName = newFileName;
	this.splitFileSize = newSplitFileSize;
	this.processingFolder = newProcessingFolder;
	
	stop(); // stop until the command's finish
	setTimeout(5000); // until timeout
	
	addCommand();
    }

    private void addCommand() throws IOException {

	// split -l 200000 filename

	final var processingAbsolutePath = processingFolder.getFile().getAbsolutePath();
	final var baseName = FilenameUtils.getBaseName(fileName);
	final var extension = FilenameUtils.getExtension(fileName);
	final var additonalSuffix = StringUtils.isEmpty(extension) ? "" : "--additional-suffix=." + extension;

	// -a5 -d
	final var command = "split -l " + splitFileSize + "  " + additonalSuffix + " " + processingAbsolutePath + separator + fileName + " " + processingAbsolutePath + separator + baseName;

	LOGGER.info("File name: '{}', Max records amount '{}'", fileName, splitFileSize);
	LOGGER.info("Split command: '{}'", command);

	setCommand(command);
    }
}
