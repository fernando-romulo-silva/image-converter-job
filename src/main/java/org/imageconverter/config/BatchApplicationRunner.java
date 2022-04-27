package org.imageconverter.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BatchApplicationRunner implements CommandLineRunner { // implements ApplicationRunner {

    private final JobLauncher jobLauncher;

    private final Job job;

    @Autowired
    BatchApplicationRunner(final JobLauncher jobLauncher, final Job job) {
	super();
	this.jobLauncher = jobLauncher;
	this.job = job;
    }

//    @Override
//    public void run(ApplicationArguments applicationArguments) throws Exception {
//	System.out.println("ApplicationRunner");
//	System.out.println(applicationArguments.getOptionNames());
//    }

    /**
     *
     */
    @Override
    public void run(final String... args) throws Exception {

	final var jobParameters = new JobParametersBuilder() //
			.addString("fileName", "2022-04-24_10-29_DBRGA.txt") //
			.toJobParameters();

	final var execution = jobLauncher.run(job, jobParameters);
	System.out.println("STATUS :: " + execution.getStatus());
    }
}
