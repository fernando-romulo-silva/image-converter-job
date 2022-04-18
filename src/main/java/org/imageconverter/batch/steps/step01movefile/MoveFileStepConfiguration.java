package org.imageconverter.batch.steps.step01movefile;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class MoveFileStepConfiguration {

    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    
    
    @Bean
    public Step sampleStep(final PlatformTransactionManager transactionManager) {
    	return this.stepBuilderFactory.get("sampleStep") //
    				.transactionManager(transactionManager) //
    				.<String, String>chunk(10) //
    				.reader(itemReader()) //
    				.writer(itemWriter()) //
    				.build();
    }

}
