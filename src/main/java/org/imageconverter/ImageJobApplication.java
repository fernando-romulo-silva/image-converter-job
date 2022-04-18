package org.imageconverter;

import org.springframework.batch.core.Job;
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
	// Pass the required Job Parameters from here to read it anywhere within
	// Spring Batch infrastructure
	JobParameters jobParameters = new JobParametersBuilder().addString("sourceDir", "C://inputLocation")
			.addString("destinationDir", "C://outputLocation").toJobParameters();

	JobExecution execution = jobLauncher.run(job, jobParameters);
	System.out.println("STATUS :: " + execution.getStatus());

    }
}
