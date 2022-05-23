package org.imageconverter.batch.step02splitfile;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SplitFileStepConfiguration {

    private final StepBuilderFactory stepBuilderFactory;

    @Autowired
    SplitFileStepConfiguration(final StepBuilderFactory stepBuilderFactory) {
	super();
	this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public Step splitFileStep(final Tasklet splitFileTasklet) {

	return this.stepBuilderFactory.get("splitFileStep") //
			.tasklet(splitFileTasklet) //
			.build();
    }
}