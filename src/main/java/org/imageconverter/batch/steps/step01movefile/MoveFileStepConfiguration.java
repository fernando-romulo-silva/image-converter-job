package org.imageconverter.batch.steps.step01movefile;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MoveFileStepConfiguration {

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Step moveFileStep(final Tasklet moveFileTasklet) {
	
	return this.stepBuilderFactory.get("moveFileStep") //
//			.transactionManager(transactionManager) //
			.tasklet(moveFileTasklet) //
			.build();
    }
}
