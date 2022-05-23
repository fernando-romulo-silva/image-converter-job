package org.imageconverter.batch.step02splitfile;

import java.util.Objects;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SplitFileStepExecutionDecider implements JobExecutionDecider {

    private static final String FLOW_STATUS_CONTINUE = "CONTINUE";

    private static final String FLOW_STATUS_SKIP = "SKIP";

    private final Logger LOGGER = null;

    private final Long splitFileSize;

    @Autowired
    SplitFileStepExecutionDecider( //
		    @Value("${application.split-file-size}") //
		    final Long splitFileSize) {
	super();
	this.splitFileSize = splitFileSize;
    }

    @Override
    public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {

	String status = FLOW_STATUS_CONTINUE;

	final boolean someCondition;
	
	if (Objects.nonNull(splitFileSize) && NumberUtils.compare(splitFileSize, 0) > 0) {
	    someCondition = true;
	} else {
	    someCondition = false;
	}

	if (someCondition == false) {

	    LOGGER.warn(jobExecution.getJobInstance().getJobName(), " -> The Step execution is disabled. Continuing with the next Step.");

	    status = FLOW_STATUS_SKIP;
	}

	return new FlowExecutionStatus(status);
    }

}
