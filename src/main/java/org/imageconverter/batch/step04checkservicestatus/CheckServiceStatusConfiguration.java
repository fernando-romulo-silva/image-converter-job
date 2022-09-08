package org.imageconverter.batch.step04checkservicestatus;

import static org.imageconverter.config.BatchConfiguration.CHECK_SERVICE_STATUS_STEP;

import org.imageconverter.util.DefaultStepListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class CheckServiceStatusConfiguration {
    
    private final StepBuilderFactory stepBuilderFactory;

    CheckServiceStatusConfiguration(final StepBuilderFactory stepBuilderFactory) {
	super();
	this.stepBuilderFactory = stepBuilderFactory;
    }

    
    @Bean
    Step checkServiceStatusStep( // 
		    final Tasklet checkServiceStatusTasklet, // 
		    final DefaultStepListener defaultStepListener, // 
		    final PlatformTransactionManager transactionManager) {

	return this.stepBuilderFactory.get(CHECK_SERVICE_STATUS_STEP) //
			.transactionManager(transactionManager) //
			.listener(defaultStepListener) //
			.tasklet(checkServiceStatusTasklet) //
			.allowStartIfComplete(true)
			.build();
    }
}
