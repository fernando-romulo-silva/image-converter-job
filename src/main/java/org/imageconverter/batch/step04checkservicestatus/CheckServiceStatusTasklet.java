package org.imageconverter.batch.step04checkservicestatus;

import static org.imageconverter.config.ImageConverterServiceConst.ACTUATOR_HEALTH_URL;

import java.util.Objects;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.imageconverter.util.http.ActuatorServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@StepScope
public class CheckServiceStatusTasklet implements Tasklet {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckServiceStatusTasklet.class);

    private final ActuatorServiceClient actuatorServiceClient;

    private final String serverURL;

    CheckServiceStatusTasklet( //
		    final ActuatorServiceClient convertImageServiceClient, //
		    //
		    @Value("${application.image-converter-service.url}") //
		    final String serverURL) {
	super();
	this.actuatorServiceClient = convertImageServiceClient;
	this.serverURL = serverURL;
    }

    @Override
    public RepeatStatus execute(final StepContribution contribution, final ChunkContext chunkContext) throws Exception {

	final var fullServerURL = serverURL + ACTUATOR_HEALTH_URL;

	LOGGER.info("Server status URL '{}'", fullServerURL);

	final var response = actuatorServiceClient.checkStatus();
	final var headers = response.getHeaders();
	
	final String csrf;
	if (Objects.nonNull(headers.get("X-CSRF-TOKEN"))) {
	    csrf = headers.get("X-CSRF-TOKEN").isEmpty() ? StringUtils.EMPTY : headers.get("X-CSRF-TOKEN").get(0);	    
	}else {
	    csrf = StringUtils.EMPTY;
	}
	
	final var jobExecutionContext = chunkContext.getStepContext() //
			.getStepExecution() //
			.getJobExecution() //
			.getExecutionContext();

	jobExecutionContext.put("CSRF", csrf);

	final var jsonString = response.getBody();

	final var mapper = new ObjectMapper();
	final var actualObj = mapper.readTree(jsonString);

	LOGGER.info("Server status is '{}'", actualObj.get("status"));

	return RepeatStatus.FINISHED;
    }
}
