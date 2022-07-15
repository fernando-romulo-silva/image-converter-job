package org.imageconverter.batch.step03loadfile;

import static java.io.File.separator;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ONE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.imageconverter.config.BatchConfiguration.LOAD_FILE_STEP_SERIAL;
import static org.springframework.batch.core.ExitStatus.COMPLETED;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.imageconverter.batch.AbstractBatchTest;
import org.imageconverter.batch.step01movefile.MoveFileStepConfiguration;
import org.imageconverter.batch.step01movefile.MoveFileStepLoggingListener;
import org.imageconverter.batch.step01movefile.MoveFileTasklet;
import org.imageconverter.batch.step02splitfile.SplitFileStepConfiguration;
import org.imageconverter.batch.step02splitfile.SplitFileStepExecutionDecider;
import org.imageconverter.batch.step02splitfile.SplitFileTasklet;
import org.imageconverter.batch.step03loadfile.parallel.LoadFilesStepParallelConfiguration;
import org.imageconverter.batch.step03loadfile.parallel.ParalellItemReader;
import org.imageconverter.batch.step03loadfile.serial.LoadFilesStepSerialConfiguration;
import org.imageconverter.batch.step03loadfile.serial.SerialItemReader;
import org.imageconverter.batch.step04convertion.ConvertionStepConfiguration;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@TestPropertySource(properties = "application.split-file-size=0")
@SpringBatchTest
@ContextConfiguration( //
		classes = { //
			DataSourceConfig.class, PersistenceJpaConfig.class, AppProperties.class, BatchConfiguration.class, // Configs
			//
			// First Step
			MoveFileStepLoggingListener.class, MoveFileTasklet.class, MoveFileStepConfiguration.class, //
			//
			// Second Step
			SplitFileStepConfiguration.class, SplitFileTasklet.class, SplitFileStepExecutionDecider.class, //
			//
			// 
			LoadFilesStepConfiguration.class, LoadFilesStepParallelConfiguration.class, LoadFilesStepSerialConfiguration.class, //
			LoadFileSetMapper.class, SerialItemReader.class, ParalellItemReader.class, LoadFileProcessor.class, LoadFileWriter.class, //
			//
			// Fourth Step
			ConvertionStepConfiguration.class
			
		} //
)
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
@TestExecutionListeners({ StepScopeTestExecutionListener.class, DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@ActiveProfiles("test")
//
@TestInstance(Lifecycle.PER_CLASS)
public class LoadFileSerialStepHappyPathTest extends AbstractBatchTest {

    
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
    void executeLoadFileSerialStep() throws IOException {
	
	// when
	final var jobExecution = jobLauncherTestUtils.launchStep(LOAD_FILE_STEP_SERIAL, defaultJobParameters());
	final var actualStepExecutions = jobExecution.getStepExecutions();
	final var actualJobExitStatus = jobExecution.getExitStatus();
	
	// then
	assertThat(actualStepExecutions.size()).isEqualTo(INTEGER_ONE);
	assertThat(actualJobExitStatus.getExitCode()).contains(COMPLETED.getExitCode());
	
    }
}
