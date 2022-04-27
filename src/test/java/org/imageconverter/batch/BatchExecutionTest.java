package org.imageconverter.batch;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Base64;

import javax.persistence.EntityManager;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.imageconverter.batch.step01movefile.MoveFileStepConfiguration;
import org.imageconverter.batch.step01movefile.MoveFileTasklet;
import org.imageconverter.config.AppProperties;
import org.imageconverter.config.BatchConfiguration;
import org.imageconverter.config.DataSourceConfig;
import org.imageconverter.config.PersistenceJpaConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.StepScopeTestExecutionListener;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
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
			DataSourceConfig.class, PersistenceJpaConfig.class, AppProperties.class, BatchConfiguration.class, //
			MoveFileStepConfiguration.class, MoveFileTasklet.class //
		} //
)
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
@TestExecutionListeners({ StepScopeTestExecutionListener.class, DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@ActiveProfiles("test")
//
@TestInstance(Lifecycle.PER_CLASS)
public class BatchExecutionTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @Autowired
    private EntityManager entityManager;

    @Value("${application.input-files-folder}")
    private Resource inputResourceResource;

    @Value("classpath:images/*.png")
    private Resource[] images;

    @BeforeAll
    void beforeAll() throws IOException {

	final var fileName = "2022-04-24_10-29_DBRGA.txt";

	int i = 1;

	final var filePath = StringUtils.remove(inputResourceResource.getURI() + File.separator + fileName, "file:");

	try (final var writer = new BufferedWriter(new FileWriter(filePath, false))) {

	    for (final var resource : images) {

		final var file1 = resource.getFile();

		final var fileContent = FileUtils.readFileToByteArray(file1);

		final var imageFileId = i;
		final var imageFileName = file1.getName();
		final var imageEncodedString = Base64.getEncoder().encodeToString(fileContent);

		final var line = imageFileId + ";" + imageFileName + ";" + imageEncodedString;

		writer.write(line);
		writer.newLine();

		i++;
	    }
	}
    }

    @AfterAll
    void afterAll() {

    }

    @AfterEach
    void cleanUp() {
	// jobRepositoryTestUtils.removeJobExecutions();
    }

    @Test
    @Order(1)
    void executeJobTest() throws Exception {

	final var jobExecution = jobLauncherTestUtils.launchJob();

	final var actualJobInstance = jobExecution.getJobInstance();

	final var actualJobExitStatus = jobExecution.getExitStatus();

	assertThat(actualJobInstance.getJobName()).isEqualTo("convertImageJob");

	assertThat(actualJobExitStatus.getExitCode()).isEqualTo("COMPLETED");
    }

    @Test
    @Disabled
    @Order(2)
    void checkValueTest() throws Exception {
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
