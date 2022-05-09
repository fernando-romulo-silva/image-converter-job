package org.imageconverter.batch.step02splitfile;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.SystemCommandTasklet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
@StepScope
public class SplitFileTasklet extends SystemCommandTasklet {

    private final String fileName;

    private final Resource processingFolder;

    SplitFileTasklet(//
		    @Value("#{jobParameters['fileName']}") //
		    final String newFileName, //
		    //
		    @Value("${application.processing-files-folder}") //
		    final Resource newProcessingFolder) throws IOException {
	super();

	this.fileName = newFileName;
	this.processingFolder = newProcessingFolder;

	addCommand();
    }

    private void addCommand() throws IOException {

	// split -l 200000 filename
	// split -l 2 -a5 -d --additional-suffix=.txt 2022-04-24_10-29_DBRGA.txt 2022-04-24_10-29_DBRGA

	final var processingAbsolutePath = processingFolder.getFile().getAbsolutePath();
	final var linesSize = 2;
	final var prefixFile = StringUtils.substringBefore(fileName, ".");

	setCommand("split -l " + linesSize + " -a5 -d --additional-suffix=.txt " + processingAbsolutePath + File.separator + fileName + " " + prefixFile);
	setTimeout(5000);
    }
}
