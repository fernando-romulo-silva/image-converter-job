package org.imageconverter.batch.step02splitfile;

import static org.apache.commons.lang3.math.NumberUtils.LONG_ZERO;

import java.util.Objects;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SplitFileStepExecutionDecider implements JobExecutionDecider {

    public static final String FLOW_STATUS_CONTINUE_PARALELL = "PARALELL";

    public static final String FLOW_STATUS_CONTINUE_SERIAL = "SERIAL";

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final Long splitFileSize;

    @Autowired
    SplitFileStepExecutionDecider( //
		    @Value("${application.split-file-size}") //
		    final Long splitFileSize) {
	super();
	this.splitFileSize = splitFileSize;
    }

    @Override
    public FlowExecutionStatus decide(final JobExecution jobExecution, final StepExecution stepExecution) {

	final String status;

	if (Objects.nonNull(splitFileSize) && NumberUtils.compare(splitFileSize, LONG_ZERO) > 0) {

	    LOGGER.warn(jobExecution.getJobInstance().getJobName(), " -> The Step 'SplitFile' execution is DISABLED. Continuing with the next Step.");

	    status = FLOW_STATUS_CONTINUE_PARALELL;
	} else {
	    
	    LOGGER.warn(jobExecution.getJobInstance().getJobName(), " -> The Step 'SplitFile' execution is ENABLED. Continuing with the this Step.");
	    
	    status = FLOW_STATUS_CONTINUE_SERIAL;
	}

	return new FlowExecutionStatus(status);
    }

}
