package org.imageconverter.batch.step01movefile;

import static java.io.File.separator;
import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ONE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.imageconverter.config.BatchConfiguration.MOVE_FILE_STEP;
import static org.springframework.batch.core.ExitStatus.COMPLETED;

import java.io.IOException;
import java.nio.file.Paths;

import org.imageconverter.batch.AbstractBatchTest;
import org.imageconverter.batch.step02splitfile.SplitFileStepExecutionDecider;
import org.imageconverter.config.AppProperties;
import org.imageconverter.config.BatchConfiguration;
import org.imageconverter.config.DataSourceConfiguration;
import org.imageconverter.config.PersistenceJpaConfig;
import org.imageconverter.util.DefaultStepListener;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
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
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

@DataJpaTest
@SpringBatchTest
@ContextConfiguration( //
		classes = { //
			// Configs
			DataSourceConfiguration.class, PersistenceJpaConfig.class, AppProperties.class, BatchConfiguration.class, 
			//
			SplitFileStepExecutionDecider.class, DefaultStepListener.class, //
			//
			// First Step: MoveFileStep
			MoveFileTasklet.class, MoveFileStepConfiguration.class, //
		} //
)
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
@TestExecutionListeners({ StepScopeTestExecutionListener.class, DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@ActiveProfiles("test")
//
@Execution(ExecutionMode.SAME_THREAD)
@TestInstance(Lifecycle.PER_CLASS)
class MoveFileStepHappyPathTest extends AbstractBatchTest {

    @BeforeAll
    void beforeAll() throws IOException {
	createBatchFile();
    }

    @AfterAll
    void afterAll() throws IOException {
	cleanFolders();
    }

    @Test
    @Order(1)
    void executeMoveFileStep() throws Exception {

	// given
	final var inputFolderAbsolutePath = Paths.get(inputFolder.getURI());
	final var processingAbsolutePath = Paths.get(processingFolder.getURI());

	final var actualFileLocation = new FileSystemResource(Paths.get(inputFolderAbsolutePath.toString() + separator + fileName));
	final var expectedFileLocation = new FileSystemResource(Paths.get(processingAbsolutePath.toString() + separator + fileName));

	assertThat(actualFileLocation.exists()).isTrue();
	assertThat(expectedFileLocation.exists()).isFalse();

	// when
	final var jobExecution = jobLauncherTestUtils.launchStep(MOVE_FILE_STEP, defaultJobParameters());
	final var actualStepExecutions = jobExecution.getStepExecutions();
	final var actualJobExitStatus = jobExecution.getExitStatus();

	// then
	assertThat(actualStepExecutions.size()).isEqualTo(INTEGER_ONE);
	assertThat(actualJobExitStatus.getExitCode()).contains(COMPLETED.getExitCode());

	assertThat(actualFileLocation.exists()).isFalse();
	assertThat(expectedFileLocation.exists()).isTrue();
    }
}
