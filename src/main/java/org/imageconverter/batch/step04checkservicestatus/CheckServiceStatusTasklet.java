package org.imageconverter.batch.step04checkservicestatus;

import static org.imageconverter.config.ImageConverterServiceConst.ACTUATOR_HEALTH_URL;

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

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

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

	final var jsonString = actuatorServiceClient.checkStatus();

	final var mapper = new ObjectMapper();
	final var actualObj = mapper.readTree(jsonString);

	LOGGER.info("Server status is '{}'", actualObj.get("status"));

	return RepeatStatus.FINISHED;
    }
}
