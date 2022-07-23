package org.imageconverter.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BatchCommandLineRunner implements CommandLineRunner {

    private final JobLauncher jobLauncher;

    private final Job job;

    BatchCommandLineRunner(final JobLauncher jobLauncher, final Job job) {
	super();
	this.jobLauncher = jobLauncher;
	this.job = job;
    }

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
