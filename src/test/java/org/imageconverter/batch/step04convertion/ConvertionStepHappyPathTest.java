package org.imageconverter.batch.step04convertion;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static java.nio.charset.Charset.forName;
import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ONE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.imageconverter.config.BatchConfiguration.CONVERTION_STEP;
import static org.springframework.batch.core.ExitStatus.COMPLETED;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import java.io.IOException;

import org.imageconverter.batch.AbstractBatchTest;
import org.imageconverter.batch.step02splitfile.SplitFileStepExecutionDecider;
import org.imageconverter.config.AppProperties;
import org.imageconverter.config.BatchConfiguration;
import org.imageconverter.config.DataSourceConfig;
import org.imageconverter.config.PersistenceJpaConfig;
import org.imageconverter.domain.BatchProcessingFileRepository;
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
import org.springframework.http.MediaType;
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
@EnableJpaRepositories(basePackageClasses = BatchProcessingFileRepository.class)
@SpringBatchTest
@ContextConfiguration( //
		classes = { //
			// Configs
			DataSourceConfig.class, PersistenceJpaConfig.class, AppProperties.class, BatchConfiguration.class, SplitFileStepExecutionDecider.class, //
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
public class ConvertionStepHappyPathTest extends AbstractBatchTest {

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(APPLICATION_JSON.getType(), APPLICATION_JSON.getSubtype(), forName("utf8"));

    public static final WireMockServer WIREMOCK = new WireMockServer(options().port(8443));
    
    @BeforeAll
    void beforeAll() throws IOException {

	jobRepositoryTestUtils = new JobRepositoryTestUtils(jobRepository, batchDataSource);

	WIREMOCK.stubFor(WireMock.post(urlEqualTo("/con_ppwnextel-war/WSConsultaSaldo?wsdl")) //
			.withRequestBody(containing("<ns1:consultaSaldoFuturo xmlns:ns1=\"http://service.console.ppware.com.br/\">")) //
			.willReturn( //
					aResponse() //
							.withStatus(200) //
							.withHeader("content-type", "text/xml") //
							.withBodyFile("cpf" + "/consultaSaldoFuturoResponse.xml") //
			));
	
	WIREMOCK.start();
    }

    @AfterAll
    void afterAll() throws IOException {
	WIREMOCK.stop();
    }
    

    @Test
    @Order(1)
    void executeLoadFileSerialStep() throws IOException {

	// given

	// when
	final var jobExecution = jobLauncherTestUtils.launchStep(CONVERTION_STEP, defaultJobParameters());
	final var actualStepExecutions = jobExecution.getStepExecutions();
	final var actualJobExitStatus = jobExecution.getExitStatus();

	// then
	assertThat(actualStepExecutions.size()).isEqualTo(INTEGER_ONE);
	assertThat(actualJobExitStatus.getExitCode()).contains(COMPLETED.getExitCode());

    }

}
