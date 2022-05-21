package org.imageconverter.batch;

import static java.io.File.separator;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ONE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.imageconverter.config.BatchConfiguration.SPLIT_FILE_STEP;
import static org.springframework.batch.core.ExitStatus.COMPLETED;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.imageconverter.batch.step02splitfile.SplitFileStepConfiguration;
import org.imageconverter.batch.step02splitfile.SplitFileTasklet;
import org.imageconverter.config.AppProperties;
import org.imageconverter.config.BatchConfiguration;
import org.imageconverter.config.DataSourceConfig;
import org.imageconverter.config.PersistenceJpaConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.StepScopeTestExecutionListener;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.FileSystemResource;
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
			SplitFileTasklet.class, SplitFileStepConfiguration.class // Second Step
		} //
)
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
@TestExecutionListeners({ StepScopeTestExecutionListener.class, DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@ActiveProfiles("test")
//
@TestInstance(Lifecycle.PER_CLASS)
class SplitFileStepHappyPathTest extends AbstractBatchTest {
    
    @BeforeAll
    void beforeAll() throws IOException {

	jobRepositoryTestUtils = new JobRepositoryTestUtils(jobRepository, batchDataSource);

	createBatchFile();
	
	final var inputFolderAbsolutePath = Paths.get(inputFolder.getURI());
	final var processingAbsolutePath = Paths.get(processingFolder.getURI());
	Files.move(//
			Paths.get(inputFolderAbsolutePath.toString() + separator + fileName), //
			Paths.get(processingAbsolutePath.toString() + separator + fileName), //
			REPLACE_EXISTING //
	);	
    }

    @AfterAll
    void afterAll() throws IOException {
	cleanFolders();
    }

    @Test
    @Order(1)
    void executeSplitFileStep() throws Exception {

	// given
	final var inputFolderAbsolutePath = Paths.get(inputFolder.getURI());
	final var processingAbsolutePath = Paths.get(processingFolder.getURI());
	
	final var actualFileLocation = new FileSystemResource(Paths.get(inputFolderAbsolutePath.toString() + separator + fileName));
	final var expectedFileLocation = new FileSystemResource(Paths.get(processingAbsolutePath.toString() + separator + fileName));
	
	assertThat(actualFileLocation.exists()).isTrue();
	assertThat(expectedFileLocation.exists()).isFalse();

	// when
	final var jobExecution = jobLauncherTestUtils.launchStep(SPLIT_FILE_STEP, defaultJobParameters());
	final var actualStepExecutions = jobExecution.getStepExecutions();
	final var actualJobExitStatus = jobExecution.getExitStatus();

	// then
	assertThat(actualStepExecutions.size()).isEqualTo(INTEGER_ONE);
	assertThat(actualJobExitStatus.getExitCode()).contains(COMPLETED.getExitCode());

	assertThat(actualFileLocation.exists()).isFalse();
	assertThat(expectedFileLocation.exists()).isTrue();
    }
}