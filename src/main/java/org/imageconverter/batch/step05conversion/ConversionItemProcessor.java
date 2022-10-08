package org.imageconverter.batch.step05conversion;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.imageconverter.domain.Image;
import org.imageconverter.util.http.ConvertImageServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

@StepScope
@Component
public class ConversionItemProcessor implements ItemProcessor<Image, Image> {

    private static final String EXECUTION_TYPE = "BATCH";

    private static final Logger LOGGER = LoggerFactory.getLogger(ConversionItemProcessor.class);

    private final ConvertImageServiceClient convertImageServiceClient;

    private final JobExecution jobExecution;

    ConversionItemProcessor( //
		    final ConvertImageServiceClient convertImageServiceClient, //
		    //
		    @Value("#{stepExecution.jobExecution}") // 
		    final JobExecution jobExecution) {
	super();
	this.convertImageServiceClient = convertImageServiceClient;
	this.jobExecution = jobExecution;
    }

    @Override
    public Image process(final Image item) {

	LOGGER.info("Item '{}' is processing", item.getName());

	final var headers = createHeader();

	final var multipartFile = createMultipartFile(item);

	final var result = convertImageServiceClient.convert(headers, multipartFile);

	item.updateConvertion(result.text());

	LOGGER.info("Item '{}' processed OK with text '{}'", item.getName(), result.text());

	return item;
    }

    private CommonsMultipartFile createMultipartFile(final Image item) {

	final var content = Base64.getDecoder() //
			.decode(item.getContent());

	final var fileItem = (DiskFileItem) new DiskFileItemFactory().createItem("file", IMAGE_PNG_VALUE, true, item.getName());

	try (//
			final var cont = new ByteArrayInputStream(content); //
			final var os = fileItem.getOutputStream()) {

	    IOUtils.copy(cont, os);

	} catch (final IOException ex) {

	    throw new IllegalArgumentException("Invalid file: " + ExceptionUtils.getRootCauseMessage(ex), ExceptionUtils.getRootCause(ex));
	}

	return new CommonsMultipartFile(fileItem);
    }

    private Map<String, String> createHeader() {

	final var jobExecutionContext = jobExecution.getExecutionContext();

	final var csr = ofNullable(jobExecutionContext.get("CSRF")).orElse(EMPTY);

	@SuppressWarnings("unchecked")
	final var cookies = ofNullable((List<String>) jobExecutionContext.get("COOKIES")).orElse(List.<String>of());

	return Map.<String, String>of( //
			"Execution-Type", EXECUTION_TYPE, //
			"X-XSRF-TOKEN", (String) csr, //
			"Cookie", cookies.stream().collect(joining(";")));
    }
}
