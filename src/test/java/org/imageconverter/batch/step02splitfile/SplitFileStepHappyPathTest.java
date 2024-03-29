package org.imageconverter.batch.step02splitfile;

import static java.io.File.separator;
import static java.math.RoundingMode.UP;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.apache.commons.lang3.StringUtils.contains;
import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ONE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.imageconverter.config.BatchConfiguration.SPLIT_FILE_STEP;
import static org.springframework.batch.core.ExitStatus.COMPLETED;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

import org.apache.commons.io.FilenameUtils;
import org.imageconverter.batch.AbstractBatchTest;
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
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

@DataJpaTest
@TestPropertySource(properties = "application.split-file-size=2")
@SpringBatchTest
@ContextConfiguration( //
		classes = { //
			// Configs
			DataSourceConfiguration.class, PersistenceJpaConfig.class, AppProperties.class, BatchConfiguration.class, //
			//
			// Other class
			SplitFileStepExecutionDecider.class, DefaultStepListener.class, //			
			//
			// Second Step: SplitFileStep
			SplitFileTasklet.class, SplitFileStepConfiguration.class //
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
    void executeSplitFileStep() throws IOException {

	// given
	final var processingAbsolutePath = Paths.get(processingFolder.getURI());

	final var baseName = FilenameUtils.getBaseName(fileName);

	final var qtyFiles = new BigDecimal(images.length).divide(new BigDecimal(splitFileSize), UP).intValue();

	final var expectedFilesNames = new ArrayList<String>(qtyFiles);
	for (var i = 97; i < 97 + qtyFiles; i++) {
	    expectedFilesNames.add(baseName + "a" + (char) i + ".txt");
	}

	// when
	final var jobExecution = jobLauncherTestUtils.launchStep(SPLIT_FILE_STEP, defaultJobParameters());
	final var actualStepExecutions = jobExecution.getStepExecutions();
	final var actualJobExitStatus = jobExecution.getExitStatus();

	// then
	assertThat(actualStepExecutions).hasSize(INTEGER_ONE);
	assertThat(actualJobExitStatus.getExitCode()).contains(COMPLETED.getExitCode());

	final var resultedFilesNames = new ArrayList<String>();
	final var filter = (Filter<Path>) p -> contains(p.getFileName().toString(), baseName) && !Objects.equals(p.getFileName().toString(), fileName);
	
	try (final var stream = Files.newDirectoryStream(processingAbsolutePath, filter)) {
	    for (final var p : stream) {
		resultedFilesNames.add(p.getFileName().toString());
	    }
	}

	assertThat(resultedFilesNames).hasSize(qtyFiles);
	assertThat(resultedFilesNames).containsAll(expectedFilesNames);
    }
}