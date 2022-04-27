package org.imageconverter.batch.step01movefile;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@StepScope
public class MoveFileTasklet implements Tasklet {

    private final String fileName;

    private final String inputFolder;

    private final String processingFolder;

    MoveFileTasklet(//
		    @Value("#{jobParameters['fileName']}") //
		    final String newFileName, //
		    //
		    @Value("${application.input-files-folder}") //
		    final String newInputFolder,
		    //
		    @Value("${application.processing-files-folder}") //
		    final String newProcessingFolder) {
	super();
	this.fileName = newFileName;
	this.inputFolder = newInputFolder;
	this.processingFolder = newProcessingFolder;
    }

    @Override
    public RepeatStatus execute(final StepContribution stepContribution, final ChunkContext chunkContext) throws IOException {

	Files.move(//
			Paths.get(inputFolder + File.separator + fileName), //
			Paths.get(processingFolder + File.separator + fileName), //
			REPLACE_EXISTING //
	);

	return RepeatStatus.FINISHED;
    }
}
