package org.imageconverter.batch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.imageconverter.config.BatchConfiguration.CONVERT_IMAGE_JOB;
import static org.springframework.batch.core.ExitStatus.COMPLETED;

import java.io.IOException;

import org.imageconverter.batch.step01movefile.MoveFileStepConfiguration;
import org.imageconverter.batch.step01movefile.MoveFileTasklet;
import org.imageconverter.batch.step02splitfile.SplitFileStepConfiguration;
import org.imageconverter.batch.step02splitfile.SplitFileStepExecutionDecider;
import org.imageconverter.batch.step02splitfile.SplitFileTasklet;
import org.imageconverter.batch.step03loadfile.LoadFileProcessor;
import org.imageconverter.batch.step03loadfile.LoadFileSetMapper;
import org.imageconverter.batch.step03loadfile.LoadFileWriter;
import org.imageconverter.batch.step03loadfile.LoadFilesStepConfiguration;
import org.imageconverter.batch.step03loadfile.parallel.LoadFilesStepParallelConfiguration;
import org.imageconverter.batch.step03loadfile.parallel.ParalellItemReader;
import org.imageconverter.batch.step03loadfile.serial.LoadFilesStepSerialConfiguration;
import org.imageconverter.batch.step03loadfile.serial.SerialItemReader;
import org.imageconverter.batch.step04checkservicestatus.CheckServiceStatusConfiguration;
import org.imageconverter.batch.step04checkservicestatus.CheckServiceStatusTasklet;
import org.imageconverter.batch.step05conversion.ConversionItemProcessor;
import org.imageconverter.batch.step05conversion.ConversionItemReader;
import org.imageconverter.batch.step05conversion.ConvertionItemWriter;
import org.imageconverter.batch.step05conversion.ConvertionStepConfiguration;
import org.imageconverter.config.AppProperties;
import org.imageconverter.config.BatchConfiguration;
import org.imageconverter.config.DataSourceConfig;
import org.imageconverter.config.OpenFeignConfiguration;
import org.imageconverter.config.PersistenceJpaConfig;
import org.imageconverter.domain.BatchProcessingFileRepository;
import org.imageconverter.util.DefaultStepListener;
import org.imageconverter.util.http.ConvertImageServiceClient;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.StepScopeTestExecutionListener;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

@DataJpaTest
@EnableJpaRepositories(basePackageClasses = BatchProcessingFileRepository.class)
@SpringBatchTest
@ContextConfiguration( //
		classes = { //
			// Configs
			DataSourceConfig.class, PersistenceJpaConfig.class, AppProperties.class, DefaultStepListener.class, ConvertImageServiceClient.class, //
			//
			// Special Configs
			OpenFeignConfiguration.class, BatchConfiguration.class, //
			//
			// First Step
			MoveFileTasklet.class, MoveFileStepConfiguration.class, //
			//
			// Second Step
			SplitFileStepConfiguration.class, SplitFileTasklet.class, SplitFileStepExecutionDecider.class, //
			//
			// Third Step
			LoadFilesStepConfiguration.class, LoadFilesStepParallelConfiguration.class, LoadFilesStepSerialConfiguration.class, //
			LoadFileSetMapper.class, SerialItemReader.class, ParalellItemReader.class, LoadFileProcessor.class, LoadFileWriter.class, //
			//
			// Fourth Step
			CheckServiceStatusTasklet.class, CheckServiceStatusConfiguration.class,
			//
			// Fifth Step
			ConvertionStepConfiguration.class, ConvertionItemWriter.class, ConversionItemProcessor.class, ConversionItemReader.class, //
		} //
)
@ImportAutoConfiguration({ FeignAutoConfiguration.class})
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
@TestExecutionListeners({ StepScopeTestExecutionListener.class, DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@ActiveProfiles("test")
//
@TestPropertySource(properties = "application.split-file-size=0")
@Execution(ExecutionMode.SAME_THREAD)
@TestInstance(Lifecycle.PER_CLASS)
class AllBatchExecutionHappyPathTest extends AbstractBatchTest {

    @BeforeAll
    void beforeAll() throws IOException {

	jobRepositoryTestUtils = new JobRepositoryTestUtils(jobRepository, batchDataSource);

	createBatchFile();
    }

    @AfterAll
    void afterAll() throws IOException {
	cleanFolders();
    }

    @AfterEach
    void cleanUp() {
	jobRepositoryTestUtils.removeJobExecutions();
    }

    @Test
    @Order(1)
    void executeJobTest() throws Exception {

	// given
	final var expectedJobName = CONVERT_IMAGE_JOB;
	final var expectedJobStatus = COMPLETED.getExitCode();

	// when
	final var jobExecution = jobLauncherTestUtils.launchJob(defaultJobParameters());
	final var actualJobInstance = jobExecution.getJobInstance();
	final var actualJobExitStatus = jobExecution.getExitStatus();

	// then
	assertThat(actualJobInstance.getJobName()).isEqualTo(expectedJobName);
	assertThat(actualJobExitStatus.getExitCode()).isEqualTo(expectedJobStatus);
    }

//    @Test
//    @Disabled
//    @Order(2)
    void checkValueTest() throws Exception {

	// given
//	    FileSystemResource expectedResult = new FileSystemResource(EXPECTED_OUTPUT);
//	    FileSystemResource actualResult = new FileSystemResource(TEST_OUTPUT);

//	jobLauncherTestUtils.launchJob();
//
//	final var idCode = entityManager//
//			.createQuery("SELECT m.codigo FROM convertionsRequest m", String.class) //
//			.getResultList(); //
//
//	final var linesImage1 = Files.lines(Paths.get(image1Resource.getURI()));
//
//	// MM/dd/yyyy hh:mm:ss
//
//	// image1.png;01/02/2018 06:07:59;
//	try (linesImage1) {
//
//	    final var idCodesT = linesImage1.map(l -> StringUtils.split(l, ";")[2])//
//			    .toList();
//
//	    assertThat(idCode).containsAll(idCodesT);
//
//	}
    }

}
