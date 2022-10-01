package org.imageconverter.batch.step05conversion;

import static com.github.tomakehurst.wiremock.client.WireMock.aMultipart;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.absent;
import static com.github.tomakehurst.wiremock.client.WireMock.binaryEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static java.nio.charset.Charset.forName;
import static org.apache.commons.io.FileUtils.readFileToByteArray;
import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ONE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.imageconverter.config.BatchConfiguration.CONVERTION_STEP;
import static org.springframework.batch.core.ExitStatus.COMPLETED;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.io.IOException;

import org.imageconverter.batch.AbstractDataBatchTest;
import org.imageconverter.batch.step02splitfile.SplitFileStepExecutionDecider;
import org.imageconverter.config.AppProperties;
import org.imageconverter.config.BatchConfiguration;
import org.imageconverter.config.DataSourceConfig;
import org.imageconverter.config.PersistenceJpaConfig;
import org.imageconverter.config.openfeign.OpenFeignSecurityConfiguration;
import org.imageconverter.domain.ImageRepository;
import org.imageconverter.service.ImageService;
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
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
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
			DataSourceConfig.class, PersistenceJpaConfig.class, AppProperties.class, BatchConfiguration.class,
			//
			// Other class
			ImageService.class, SplitFileStepExecutionDecider.class, DefaultStepListener.class, //
			//
			// Special Configs
			OpenFeignSecurityConfiguration.class,
			//
			// Fifth Step
			ConvertionItemWriter.class, ConversionItemProcessor.class, ConversionItemReader.class, ConvertionStepConfiguration.class

		} //
)
@ImportAutoConfiguration({ FeignAutoConfiguration.class })
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
@TestExecutionListeners({ StepScopeTestExecutionListener.class, DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@ActiveProfiles("test")
@TestPropertySource(properties = "application.split-file-size=4")
//
@TestInstance(Lifecycle.PER_CLASS)
public class ConvertionStepHappyPathTest extends AbstractDataBatchTest {

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(APPLICATION_JSON.getType(), APPLICATION_JSON.getSubtype(), forName("utf8"));

    public final WireMockServer wireMockServer = new WireMockServer(options().port(8989));

    // #header /rest/images/conversion/1
    @BeforeAll
    void beforeAll() throws IOException {

	jobRepositoryTestUtils = new JobRepositoryTestUtils(jobRepository, batchDataSource);

	createBatchDb();

	// MultipartFile[field="file", filename=01_best.png, contentType=image/png, size=835]
	
	// http://127.0.0.1:8080/rest/images/conversion

	for (final var image : images) {

	    wireMockServer.stubFor( //
			    WireMock.post(urlEqualTo("/rest/images/convertion")) //
					    .withHeader("X-CSRF-TOKEN", absent()) //
					    .withHeader("Content-Type", containing("multipart/form-data;")) //
					    .withHeader("Content-Length", containing(Long.toString(image.contentLength()))) //
					    .withMultipartRequestBody( //
							    aMultipart() //
							        .withName("file") //
							        .withBody(binaryEqualTo(readFileToByteArray(image.getFile())))) //
					    .willReturn( //
							    aResponse() //
								.withStatus(200) //
								.withHeader("Content-Type", "application/json") //
								.withBody("{ \"text\": \"best\" }") //
					    ) //
	    );

	}

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
	final var jobExecution = jobLauncherTestUtils.launchStep(CONVERTION_STEP, defaultJobParameters());

	// when
	final var actualStepExecutions = jobExecution.getStepExecutions();
	final var actualJobExitStatus = jobExecution.getExitStatus();

	// then
	assertThat(actualStepExecutions.size()).isEqualTo(INTEGER_ONE);
	assertThat(actualJobExitStatus.getExitCode()).contains(COMPLETED.getExitCode());

    }
}
