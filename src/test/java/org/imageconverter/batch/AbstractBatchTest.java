package org.imageconverter.batch;

import static java.io.File.separator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Base64;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.apache.commons.io.FileUtils;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

public abstract class AbstractBatchTest {

    @Autowired
    protected JobLauncherTestUtils jobLauncherTestUtils;

//    @Autowired
    protected JobRepositoryTestUtils jobRepositoryTestUtils;

    @Autowired
    protected JobRepository jobRepository;

    @Qualifier("batchDataSource")
    @Autowired
    protected DataSource batchDataSource;

    @Autowired
    protected EntityManager entityManager;

    @Value("${application.batch-folders.input-files}")
    protected Resource inputFolder;

    @Value("${application.batch-folders.processing-files}") //
    protected Resource processingFolder;

    @Value("${application.batch-folders.error-files}") //
    protected Resource errorFolder;

    @Value("${application.batch-folders.processed-files}") //
    protected Resource processedFolder;

    @Value("classpath:images/*.png")
    protected Resource[] images;

    protected String fileName = "2022-04-24_10-29_DBRGA.txt";
    
    protected Long qtyImages = 0L;

    protected void createBatchFile() throws IOException {
	
	cleanFolders();
	
	long i = 0;

	final var filePath = inputFolder.getFile().getAbsolutePath() + separator + fileName;

	try (final var writer = new BufferedWriter(new FileWriter(filePath, false))) {

	    for (final var resource : images) {

		final var file = resource.getFile();

		final var fileContent = FileUtils.readFileToByteArray(file);

		final var imageFileId = i;
		final var imageFileName = file.getName();
		final var imageEncodedString = Base64.getEncoder().encodeToString(fileContent);

		final var line = imageFileId + ";" + imageFileName + ";" + imageEncodedString;

		writer.write(line);
		writer.newLine();

		i++;
	    }
	}
	
	qtyImages = i;
    }

    protected void cleanFolders() throws IOException {
	FileUtils.cleanDirectory(inputFolder.getFile());
	FileUtils.cleanDirectory(processingFolder.getFile());
	FileUtils.cleanDirectory(errorFolder.getFile());
	FileUtils.cleanDirectory(processedFolder.getFile());
    }

    protected JobParameters defaultJobParameters() {
	final var paramsBuilder = new JobParametersBuilder();
	paramsBuilder.addString("fileName", fileName);
	return paramsBuilder.toJobParameters();
    }
}
