package org.imageconverter.batch;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.file.Paths;

import org.imageconverter.batch.step01movefile.MoveFileStepConfiguration;
import org.imageconverter.batch.step01movefile.MoveFileStepLoggingListener;
import org.imageconverter.batch.step01movefile.MoveFileTasklet;
import org.imageconverter.config.AppProperties;
import org.imageconverter.config.BatchConfiguration;
import org.imageconverter.config.DataSourceConfig;
import org.imageconverter.config.PersistenceJpaConfig;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.test.StepScopeTestExecutionListener;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@SpringBatchTest
@ContextConfiguration( //
		classes = { //
			DataSourceConfig.class, PersistenceJpaConfig.class, AppProperties.class, BatchConfiguration.class, // Configs
			MoveFileStepLoggingListener.class, MoveFileTasklet.class, MoveFileStepConfiguration.class // First Step
		} //
)
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
@TestExecutionListeners({ StepScopeTestExecutionListener.class, DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@ActiveProfiles("test")
//
@TestInstance(Lifecycle.PER_CLASS)
public class MoveFileStepHappyPathTest extends AbstractBatchTest {
    
    
    @Value("#{jobParameters['fileName']}") //
    private String fileName;

    @Value("${application.input-files-folder}") //
    private Resource inputFolder;

    @Value("${application.processing-files-folder}") //
    private Resource processingFolder;

    private static final String EXPECTED_OUTPUT = null;
    
    private static final String TEST_OUTPUT = null;

    @Test
    @Order(1)
    public void givenReferenceOutput_whenStep1Executed_thenSuccess() throws Exception {
	// given
	final var inputFolderAbsolutePath = Paths.get(inputFolder.getURI());
	final var processingAbsolutePath = Paths.get(processingFolder.getURI());
	
	final var expectedResult = new FileSystemResource(Paths.get(processingAbsolutePath.toString() + File.separator + fileName));
	final var actualResult = new FileSystemResource(TEST_OUTPUT);

	// when
	final var jobExecution = jobLauncherTestUtils.launchStep("step1", defaultJobParameters());
	final var actualStepExecutions = jobExecution.getStepExecutions();
	final var actualJobExitStatus = jobExecution.getExitStatus();

	// then
	assertThat(actualStepExecutions.size()).isEqualTo(1);
	assertThat(actualJobExitStatus.getExitCode()).contains("COMPLETED");
	
	assertThat(inputFolderAbsolutePath).isDirectory();
	
	//AssertFile.assertFileEquals(expectedResult, actualResult);
    }

}
