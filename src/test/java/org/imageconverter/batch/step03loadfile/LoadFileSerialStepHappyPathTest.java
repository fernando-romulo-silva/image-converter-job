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
import java.util.List;
import java.util.Map;

import org.imageconverter.application.ImageService;
import org.imageconverter.batch.AbstractDataBatchTest;
import org.imageconverter.batch.step02splitfile.SplitFileStepExecutionDecider;
import org.imageconverter.batch.step03loadfile.serial.LoadFilesStepSerialConfiguration;
import org.imageconverter.batch.step03loadfile.serial.SerialItemReader;
import org.imageconverter.config.AppProperties;
import org.imageconverter.config.BatchConfiguration;
import org.imageconverter.config.DataSourceConfiguration;
import org.imageconverter.config.PersistenceJpaConfig;
import org.imageconverter.domain.Image;
import org.imageconverter.domain.ImageRepository;
import org.imageconverter.util.DefaultStepListener;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
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
@EnableJpaRepositories(basePackageClasses = ImageRepository.class)
@TestPropertySource(properties = "application.split-file-size=0")
@SpringBatchTest
@ContextConfiguration( //
		classes = { //
			// Configs
			DataSourceConfiguration.class, PersistenceJpaConfig.class, AppProperties.class, BatchConfiguration.class, //
			//
			// Other class
			ImageService.class, SplitFileStepExecutionDecider.class, DefaultStepListener.class, //			
			//
			// Fourth Step 3.1: LoadFileSerial
			LoadFileProcessor.class, LoadFileSetMapper.class, LoadFileWriter.class, //
			LoadFilesStepConfiguration.class, LoadFilesStepSerialConfiguration.class, SerialItemReader.class //

		} //
)
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
@TestExecutionListeners({ StepScopeTestExecutionListener.class, DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@ActiveProfiles("test")
//
@TestInstance(Lifecycle.PER_CLASS)
class LoadFileSerialStepHappyPathTest extends AbstractDataBatchTest {
    
    private List<Map.Entry<String, String>> imagesDTO;

    @BeforeAll
    void beforeAll() throws IOException {

	imagesDTO = createBatchFile();

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
	
	// given
	final var idImagesList = imagesDTO.stream().map(elem -> elem.getKey()).toList();
	final var nameImagesList = imagesDTO.stream().map(elem -> elem.getValue()).toList();

	// when
	final var jobExecution = jobLauncherTestUtils.launchStep(LOAD_FILE_STEP_SERIAL, defaultJobParameters());
	final var actualStepExecutions = jobExecution.getStepExecutions();
	final var actualJobExitStatus = jobExecution.getExitStatus();

	// then
	assertThat(actualStepExecutions.size()).isEqualTo(INTEGER_ONE); // one step execution
	assertThat(actualJobExitStatus.getExitCode()).contains(COMPLETED.getExitCode());
	
	final var dbList = entityManager.createQuery("Select i from Image i", Image.class).getResultList();

	assertThat(dbList)//
		.hasSize(imagesDTO.size()) //
		.allMatch(element -> idImagesList.contains(Long.toString(element.getId())))
		.allMatch(element -> nameImagesList.contains(element.getName()));
	
//	assertThat(dbList.size()).isEqualByComparingTo(imagesDTO.size());
//	assertThat(dbList).map(d -> Long.toString(d.getId())).containsAll(idImagesList);
//	assertThat(dbList).map(d -> d.getName()).containsAll(nameImagesList);
    }
}
