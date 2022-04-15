package org.imageconverter.batch;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Paths;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;
import org.imageconverter.config.AppProperties;
import org.imageconverter.config.DataSourceConfig;
import org.imageconverter.config.PersistenceJpaConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
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
@ContextConfiguration(classes = { DataSourceConfig.class, PersistenceJpaConfig.class, AppProperties.class })
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@ActiveProfiles("test")
public class BatchExecutionTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @Autowired
    private EntityManager entityManager;

    @Value("${file.scau.input}")
    private Resource image1Resource;

    @AfterEach
    void cleanUp() {
	jobRepositoryTestUtils.removeJobExecutions();
    }

    @Test
    @Order(1)
    void executeJobTest() throws Exception {

	final var jobExecution = jobLauncherTestUtils.launchJob();

	final var actualJobInstance = jobExecution.getJobInstance();

	final var actualJobExitStatus = jobExecution.getExitStatus();

	assertThat(actualJobInstance.getJobName()).isEqualTo("");

	assertThat(actualJobExitStatus.getExitCode()).isEqualTo("COMPLETED");
    }

    @Test
    @Order(2)
    void checkValueTest() throws Exception {
	jobLauncherTestUtils.launchJob();

	final var idCode = entityManager//
			.createQuery("SELECT m.codigo FROM convertionsRequest m", String.class) //
			.getResultList(); //

	final var linesImage1 = Files.lines(Paths.get(image1Resource.getURI()));
	
	// MM/dd/yyyy hh:mm:ss
	
	// image1.png;01/02/2018 06:07:59;
	try (linesImage1) {

	    final var idCodesT = linesImage1.map(l -> StringUtils.split(l, ";")[2])//
			    .toList();

	    assertThat(idCode).containsAll(idCodesT);

	}

    }
}
