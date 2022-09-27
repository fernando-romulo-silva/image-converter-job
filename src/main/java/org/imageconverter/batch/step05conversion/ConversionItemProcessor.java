package org.imageconverter.batch.step05conversion;

import static java.util.stream.Collectors.joining;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.imageconverter.domain.Image;
import org.imageconverter.util.http.ConvertImageServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

@StepScope
@Component
public class ConversionItemProcessor implements ItemProcessor<Image, Image> {

    private static final String EXECUTION_TYPE = "BATCH";

    private static final Logger LOGGER = LoggerFactory.getLogger(ConversionItemProcessor.class);

    private final ConvertImageServiceClient convertImageServiceClient;

    @Value("#{stepExecution.jobExecution}")
    private JobExecution jobExecution;

    ConversionItemProcessor(final ConvertImageServiceClient convertImageServiceClient) {
	super();
	this.convertImageServiceClient = convertImageServiceClient;
    }

    public Image process(final Image item) throws Exception {

	LOGGER.info("Item {} is processing", item.getName());

	final var jobExecutionContext = jobExecution.getExecutionContext();

	final var csr = Optional.ofNullable(jobExecutionContext.get("CSRF")).orElse(StringUtils.EMPTY);

	@SuppressWarnings("unchecked")
	final var cookies = (List<String>) jobExecutionContext.get("COOKIES");

	final var headers = Map.<String, String>of( //
			"X-XSRF-TOKEN", (String) csr, //
			"Cookie", cookies.stream().collect(joining(";"))
	);

	final var content = Base64.getDecoder().decode(item.getContent());

	

	final var fileItem = (DiskFileItem) new DiskFileItemFactory().createItem("file", MediaType.IMAGE_PNG_VALUE, true, item.getName());

	try (//
		final var cont = new ByteArrayInputStream(content);// 
		final var os = fileItem.getOutputStream()) {
	    
	    IOUtils.copy(cont, os);
	    
	} catch (final IOException ex) {
	    throw new IllegalArgumentException("Invalid file: " + ex, ex);
	}

	final var multipartFile = new CommonsMultipartFile(fileItem);

	final var result = convertImageServiceClient.convert(headers, multipartFile);

	item.updateConvertion(result.text());

	LOGGER.info("Item {} processed OK", item.getName());

	return item;
    }

}
