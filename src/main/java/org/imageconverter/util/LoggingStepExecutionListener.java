package org.imageconverter.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

public class LoggingStepExecutionListener implements StepExecutionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingStepExecutionListener.class);

    @Override
    public ExitStatus afterStep(final StepExecution stepExecution) {
	LOGGER.info("{} has ended with {} status", stepExecution.getStepName(), stepExecution.getExitStatus());
	return stepExecution.getExitStatus();
    }

    @Override
    public void beforeStep(final StepExecution stepExecution) {
	LOGGER.info("{} has begun!", stepExecution.getStepName());
    }
}
