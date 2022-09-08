package org.imageconverter.batch.step04checkservicestatus;

import org.imageconverter.util.http.ConvertImageServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
@StepScope
public class CheckServiceStatusTasklet implements Tasklet {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final ConvertImageServiceClient convertImageServiceClient;

    CheckServiceStatusTasklet(final ConvertImageServiceClient convertImageServiceClient) {
	super();
	this.convertImageServiceClient = convertImageServiceClient;
    }

    @Override
    public RepeatStatus execute(final StepContribution contribution, final ChunkContext chunkContext) throws Exception {

	LOGGER.info("File name: '{}', Max records amount '{}'", contribution);
	LOGGER.info("Split command: '{}'", chunkContext);

	return RepeatStatus.FINISHED;
    }

}
