package org.imageconverter.batch.step01movefile;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.stereotype.Component;

@Component
public class MoveFileStepLoggingListener {

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
	System.out.println(stepExecution.getStepName() + " has begun!");
    }

    @AfterStep
    public ExitStatus afterStep(StepExecution stepExecution) {
	System.out.println(stepExecution.getStepName() + " has ended!");
	return stepExecution.getExitStatus();
    }
}
