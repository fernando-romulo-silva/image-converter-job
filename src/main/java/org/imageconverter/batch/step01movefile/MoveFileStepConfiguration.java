package org.imageconverter.batch.step01movefile;

import static org.imageconverter.config.BatchConfiguration.MOVE_FILE_STEP;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class MoveFileStepConfiguration {

    private final StepBuilderFactory stepBuilderFactory;

    @Autowired
    MoveFileStepConfiguration(final StepBuilderFactory stepBuilderFactory) {
	super();
	this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    Step moveFileStep(final Tasklet moveFileTasklet, final MoveFileStepLoggingListener moveFileStepLoggingListener, final PlatformTransactionManager transactionManager) {

	return this.stepBuilderFactory.get(MOVE_FILE_STEP) //
			.transactionManager(transactionManager) //
			.listener(moveFileStepLoggingListener) //
			.tasklet(moveFileTasklet) //
			.build();
    }
}
