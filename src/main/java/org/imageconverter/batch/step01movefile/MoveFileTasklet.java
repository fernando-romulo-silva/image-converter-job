package org.imageconverter.batch.step01movefile;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.imageconverter.domain.BatchProcessingFile;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@StepScope
public class MoveFileTasklet implements Tasklet {

    private final String fileName;

    private final Resource inputFolder;

    private final Resource processingFolder;

    @PersistenceContext
    private EntityManager entityManager;

    MoveFileTasklet(//
		    @Value("#{jobParameters['fileName']}") //
		    final String newFileName, //
		    //
		    @Value("${application.batch-folders.input-files}") //
		    final Resource newInputFolder,
		    //
		    @Value("${application.batch-folders.processing-files}") //
		    final Resource newProcessingFolder) {
	super();
	this.fileName = newFileName;
	this.inputFolder = newInputFolder;
	this.processingFolder = newProcessingFolder;
    }

    @Override
    public RepeatStatus execute(final StepContribution stepContribution, final ChunkContext chunkContext) throws IOException {

	final var inputFolderAbsolutePath = Paths.get(inputFolder.getURI());
	final var processingAbsolutePath = Paths.get(processingFolder.getURI());

	Files.move(//
			Paths.get(inputFolderAbsolutePath.toString() + File.separator + fileName), //
			Paths.get(processingAbsolutePath.toString() + File.separator + fileName), //
			REPLACE_EXISTING //
	);

	saveBatchProcessingFile();
	
	return RepeatStatus.FINISHED;
    }

    @Transactional
    public void saveBatchProcessingFile() {
	final var file = new BatchProcessingFile(fileName);
	entityManager.merge(file);
    }
}
