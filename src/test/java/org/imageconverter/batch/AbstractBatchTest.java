package org.imageconverter.batch;

import static java.io.File.separator;
import static java.math.RoundingMode.UP;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import com.google.common.collect.Lists;

public abstract class AbstractBatchTest {

    @Autowired
    protected JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    protected JobRepositoryTestUtils jobRepositoryTestUtils;

    @Autowired
    protected JobRepository jobRepository;

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

    @Value("${application.split-file-size}")
    protected Long splitFileSize;

    @Value("${application.image-converter-service.url}")
    protected String serverURL;

    protected String fileName = "2022-04-24_10-29_DBRGA.txt";
    
    protected List<Entry<String, String>> createBatchFile() throws IOException {

	var i = 1L;

	final var result = new ArrayList<Entry<String, String>>();

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

		result.add(new SimpleEntry<>(String.valueOf(imageFileId), imageFileName));

		i++;
	    }
	}

	return result;
    }

    protected List<Entry<String, String>> createSpliptedBatchFile() throws IOException {

	final var result = new ArrayList<Entry<String, String>>();

	final var processingAbsolutePath = Paths.get(processingFolder.getURI());

	final var baseName = FilenameUtils.getBaseName(fileName);

	final var qtyFiles = new BigDecimal(images.length).divide(new BigDecimal(splitFileSize), UP).intValue();

	final var expectedFilesNames = new ArrayList<String>(qtyFiles);
	for (var i = 97; i < 97 + qtyFiles; i++) {
	    expectedFilesNames.add(baseName + "a" + (char) i + ".txt");
	}

	final var imagesList = Arrays.asList(images);
	final var imagesListsGroups = Lists.partition(imagesList, splitFileSize.intValue());
	var filePos = 0;
	var i = 1L;

	for (final var imagesLists : imagesListsGroups) {

	    final var fileName = processingAbsolutePath + separator + expectedFilesNames.get(filePos);

	    try (final var writer = new BufferedWriter(new FileWriter(fileName, false))) {

		for (final var resource : imagesLists) {

		    final var file = resource.getFile();

		    final var fileContent = FileUtils.readFileToByteArray(file);

		    final var imageFileId = i;
		    final var imageFileName = file.getName();
		    final var imageEncodedString = Base64.getEncoder().encodeToString(fileContent);

		    final var line = imageFileId + ";" + imageFileName + ";" + imageEncodedString;

		    writer.write(line);
		    writer.newLine();

		    i++;

		    result.add(new SimpleEntry<>(String.valueOf(imageFileId), imageFileName));
		}
	    }

	    filePos++;
	}

	return result;
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
