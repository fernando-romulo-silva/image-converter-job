package org.imageconverter;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application's starter, just a main class
 * 
 * @author Fernando Romulo da Silva
 */
@SpringBootApplication
public class ImageJobApplication implements CommandLineRunner {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    /**
     * Main method.
     * 
     * @param args the application arguments
     */
    public static void main(String[] args) {
	SpringApplication.run(ImageJobApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
	// Pass the required Job Parameters from here to read it anywhere within Spring Batch infrastructure
	
	final var jobParameters = new JobParametersBuilder() //
			.addString("fileName", "/home/fernando/Development/workspaces/eclipse-workspace/image-converter-job/target/test-classes/folders/input/2022-04-24_10-29_DBRGA.txt") //
			.toJobParameters();

	final var execution = jobLauncher.run(job, jobParameters);
	System.out.println("STATUS :: " + execution.getStatus());

    }
}
