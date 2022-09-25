package org.imageconverter.batch.step04checkservicestatus;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ONE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.imageconverter.config.BatchConfiguration.CHECK_SERVICE_STATUS_STEP;
import static org.imageconverter.config.ImageConverterServiceConst.ACTUATOR_HEALTH_URL;
import static org.springframework.batch.core.ExitStatus.COMPLETED;

import java.io.IOException;
import java.util.UUID;

import org.imageconverter.batch.AbstractBatchTest;
import org.imageconverter.batch.step02splitfile.SplitFileStepExecutionDecider;
import org.imageconverter.config.AppProperties;
import org.imageconverter.config.BatchConfiguration;
import org.imageconverter.config.DataSourceConfig;
import org.imageconverter.config.PersistenceJpaConfig;
import org.imageconverter.config.openfeign.OpenFeignConfiguration;
import org.imageconverter.domain.ImageRepository;
import org.imageconverter.util.DefaultStepListener;
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
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

@DataJpaTest
@EnableJpaRepositories(basePackageClasses = ImageRepository.class)
@SpringBatchTest
@ContextConfiguration( //
		classes = { //
			// Configs
			DataSourceConfig.class, PersistenceJpaConfig.class, AppProperties.class, BatchConfiguration.class, SplitFileStepExecutionDecider.class, DefaultStepListener.class, //
			//
			// Special Configs
			OpenFeignConfiguration.class,
			//
			// Fourth Step
			CheckServiceStatusTasklet.class, CheckServiceStatusConfiguration.class
		} //
)
@ImportAutoConfiguration({ FeignAutoConfiguration.class })
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
@TestExecutionListeners({ StepScopeTestExecutionListener.class, DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@ActiveProfiles("test")
//
@TestInstance(Lifecycle.PER_CLASS)
public class CheckServiceStatusHappyPathTest extends AbstractBatchTest {

    WireMockServer wireMockServer;

    @BeforeAll
    void beforeAll() throws IOException {

	jobRepositoryTestUtils = new JobRepositoryTestUtils(jobRepository, batchDataSource);
	
	final var serverPort = Integer.parseInt(serverURL.split(":")[2]);

	wireMockServer = new WireMockServer(options().port(serverPort));

	wireMockServer.stubFor(WireMock.get(urlEqualTo(ACTUATOR_HEALTH_URL)) //
			.willReturn( //
					aResponse() //
							.withStatus(200) //
							.withHeader("content-type", "text/json") //
							.withHeader("X-CSRF-TOKEN", UUID.randomUUID().toString())
							.withBodyFile("get-health-200.json") //
							.withChunkedDribbleDelay(5, 9000)
			));

	wireMockServer.start();
    }

    @AfterAll
    void afterAll() throws IOException {
	wireMockServer.stop();
    }

    @Test
    @Order(1)
    void executeLoadFileSerialStep() throws IOException {

	// given
	final var jobExecution = jobLauncherTestUtils.launchStep(CHECK_SERVICE_STATUS_STEP, defaultJobParameters());

	// when
	final var actualStepExecutions = jobExecution.getStepExecutions();
	final var actualJobExitStatus = jobExecution.getExitStatus();

	// then
	assertThat(actualStepExecutions.size()).isEqualTo(INTEGER_ONE);
	assertThat(actualJobExitStatus.getExitCode()).contains(COMPLETED.getExitCode());
    }
}
