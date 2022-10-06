package org.imageconverter.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.stereotype.Component;

@Component
public class DefaultStepListener {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultStepListener.class);

    @BeforeStep
    public void beforeStep(final StepExecution stepExecution) {
	LOGGER.info("=====================================================================================");
	LOGGER.info("Step '{}' started!", stepExecution.getStepName());
	LOGGER.info("Step '{}' start time '{}'", stepExecution.getStepName(), stepExecution.getStartTime());
    }

    @AfterStep
    public ExitStatus afterStep(final StepExecution stepExecution) {
        LOGGER.info("Read count '{}'", stepExecution.getReadCount());
        LOGGER.info("Skip count '{}'", stepExecution.getSkipCount());
        LOGGER.info("Commit count '{}'", stepExecution.getCommitCount());
        LOGGER.info("Step '{}' finished!", stepExecution.getStepName());
        LOGGER.info("Step '{}' end time '{}'", stepExecution.getStepName(), stepExecution.getEndTime());
//        LOGGER.info("=====================================================================================");
        return stepExecution.getExitStatus();
    }
}
