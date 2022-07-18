package org.imageconverter.batch.step03loadfile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.imageconverter.config.BatchConfiguration.LOAD_FILE_STEP_PARALELL;
import static org.springframework.batch.core.ExitStatus.COMPLETED;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.imageconverter.batch.AbstractBatchTest;
import org.imageconverter.batch.step02splitfile.SplitFileStepExecutionDecider;
import org.imageconverter.batch.step03loadfile.parallel.LoadFilesStepParallelConfiguration;
import org.imageconverter.batch.step03loadfile.parallel.ParalellItemReader;
import org.imageconverter.config.AppProperties;
import org.imageconverter.config.BatchConfiguration;
import org.imageconverter.config.DataSourceConfig;
import org.imageconverter.config.PersistenceJpaConfig;
import org.imageconverter.domain.BatchProcessingFileRepository;
import org.imageconverter.domain.Image;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.StepScopeTestExecutionListener;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
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
@TestPropertySource(properties = "application.split-file-size=2")
@SpringBatchTest
@ContextConfiguration( //
		classes = { //
			// Configs
			DataSourceConfig.class, PersistenceJpaConfig.class, AppProperties.class, BatchConfiguration.class, SplitFileStepExecutionDecider.class, //
			//
			// Fourth Step 3.2: LoadFileParallel
			LoadFileProcessor.class, LoadFileSetMapper.class, LoadFileWriter.class, //
			LoadFilesStepConfiguration.class, LoadFilesStepParallelConfiguration.class, ParalellItemReader.class, } //
)
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
@TestExecutionListeners({ StepScopeTestExecutionListener.class, DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@ActiveProfiles("test")
//
@TestInstance(Lifecycle.PER_CLASS)
class LoadFileParalellStepHappyPathTest extends AbstractBatchTest {
    
    private List<Map.Entry<Long, String>> imagesDTO;

    @BeforeAll
    void beforeAll() throws IOException {

	jobRepositoryTestUtils = new JobRepositoryTestUtils(jobRepository, batchDataSource);
	
	imagesDTO = createSpliptedBatchFile();
    }

    @AfterAll
    void afterAll() throws IOException {
	cleanFolders();
    }

    @Test
    @Order(1)
    void executeLoadFileSerialStep() throws IOException {

	// given
	final var qtImages = imagesDTO.stream().count();
	
	// when
	final var jobExecution = jobLauncherTestUtils.launchStep(LOAD_FILE_STEP_PARALELL, defaultJobParameters());
	final var actualStepExecutions = jobExecution.getStepExecutions();
	final var actualJobExitStatus = jobExecution.getExitStatus();
	
	final var dbList = entityManager.createQuery(Image.class, "Select i Image i").getResultList();

	// then
	assertThat(actualStepExecutions.size()).isEqualTo(splitFileSize.intValue() + 1); // qtdy file == number of executions
	assertThat(actualJobExitStatus.getExitCode()).contains(COMPLETED.getExitCode());
    }

}
