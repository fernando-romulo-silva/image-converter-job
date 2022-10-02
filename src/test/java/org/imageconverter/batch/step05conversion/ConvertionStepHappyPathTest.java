package org.imageconverter.batch.step05conversion;

import static com.github.tomakehurst.wiremock.client.WireMock.aMultipart;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.binaryEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.apache.commons.io.FileUtils.readFileToByteArray;
import static org.apache.commons.lang3.StringUtils.splitByCharacterTypeCamelCase;
import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ONE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.imageconverter.config.BatchConfiguration.CONVERTION_STEP;
import static org.imageconverter.config.ImageConverterServiceConst.CONVERTION_URL;
import static org.springframework.batch.core.ExitStatus.COMPLETED;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
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

    private WireMockServer wireMockServer;

    // #header /rest/images/conversion/1
    @BeforeAll
    void beforeAll() throws IOException {

	jobRepositoryTestUtils = new JobRepositoryTestUtils(jobRepository, batchDataSource);

	createBatchDb();
	
	final var serverPort = Integer.parseInt(serverURL.split(":")[2]);

	wireMockServer = new WireMockServer(options().port(serverPort));

	final var notPermitedWords = List.of(".", "_", "png");
	
	for (final var image : images) {
	    
	   final var fileSplit1 = StringUtils.split(image.getFilename(), "_");
	   
	   final var fileId = fileSplit1[0];
	    
	   final var resultTextArray = splitByCharacterTypeCamelCase(fileSplit1[1]);
	   
	   final var resultText = Stream.of(resultTextArray)
			   .filter(s -> !notPermitedWords.contains(s))
			   .collect(Collectors.joining(" "));
	   
	    wireMockServer.stubFor( //
			    WireMock.post(urlEqualTo(CONVERTION_URL)) //
//					    .withHeader("X-CSRF-TOKEN", absent()) //
					    .withHeader("Content-Type", containing("multipart/form-data; charset=UTF-8;")) //
//					    .withHeader("Content-Length", containing(Long.toString(image.contentLength()))) //
					    .withMultipartRequestBody( //
							    aMultipart() //
							        .withName("file") //
							        .withBody(binaryEqualTo(readFileToByteArray(image.getFile())))) //
					    .willReturn( //
							    aResponse() //
								.withStatus(200) //
								.withHeader("Location", CONVERTION_URL + "/" + Integer.parseInt(fileId)) //
								.withHeader("Content-Type", "application/json") //
								.withBody("{ \"text\": \""+ resultText +"\" }") //
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
