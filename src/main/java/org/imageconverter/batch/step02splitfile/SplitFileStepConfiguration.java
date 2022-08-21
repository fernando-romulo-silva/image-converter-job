package org.imageconverter.batch.step02splitfile;

import org.imageconverter.util.DefaultStepListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SplitFileStepConfiguration {

    private final StepBuilderFactory stepBuilderFactory;

    SplitFileStepConfiguration(final StepBuilderFactory stepBuilderFactory) {
	super();
	this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    Step splitFileStep(final Tasklet splitFileTasklet, final DefaultStepListener defaultStepListener) {

	return this.stepBuilderFactory.get("splitFileStep") //
			.listener(defaultStepListener) //
			.tasklet(splitFileTasklet) //
			.build();
    }
}
