package org.imageconverter.batch.steps.step02splitfile;

import java.io.File;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.SystemCommandTasklet;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SplitConfiguration {

    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    
    @Bean
    public Step stepSplitFile(final Tasklet splitTasklet) {
	
        return this.stepBuilderFactory.get("stepSplitFile") //
        			.tasklet(splitTasklet) //
        			.build();
        
    }
    
    @Bean
    @StepScope
    public SystemCommandTasklet splitTasklet(@Value("#{jobParameters['file']}") final File file) {
    	final var tasklet = new SystemCommandTasklet();

    	tasklet.setCommand("echo hello");
    	tasklet.setTimeout(5000);

    	return tasklet;
    }

}
