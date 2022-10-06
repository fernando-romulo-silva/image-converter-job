package org.imageconverter.batch;

import static com.github.tomakehurst.wiremock.client.WireMock.aMultipart;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.binaryEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.io.FileUtils.readFileToByteArray;
import static org.apache.commons.lang3.StringUtils.splitByCharacterTypeCamelCase;
import static org.assertj.core.api.Assertions.assertThat;
import static org.imageconverter.config.BatchConfiguration.CONVERT_IMAGE_JOB;
import static org.imageconverter.config.ImageConverterServiceConst.ACTUATOR_HEALTH_URL;
import static org.imageconverter.config.ImageConverterServiceConst.CONVERTION_URL;
import static org.springframework.batch.core.ExitStatus.COMPLETED;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.imageconverter.application.ImageService;
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
import org.imageconverter.config.PersistenceJpaConfig;
import org.imageconverter.config.openfeign.OpenFeignConfiguration;
import org.imageconverter.domain.ImageRepository;
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

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

@DataJpaTest
@EnableJpaRepositories(basePackageClasses = ImageRepository.class)
@SpringBatchTest
@ContextConfiguration( //
		classes = { //
			// Configs
			DataSourceConfig.class, PersistenceJpaConfig.class, AppProperties.class, ConvertImageServiceClient.class, //
			// 
			// Other class
			ImageService.class, DefaultStepListener.class, //			
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
@ImportAutoConfiguration({ FeignAutoConfiguration.class })
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
@TestExecutionListeners({ StepScopeTestExecutionListener.class, DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@ActiveProfiles("test")
//
@TestPropertySource(properties = "application.split-file-size=0")
@Execution(ExecutionMode.SAME_THREAD)
@TestInstance(Lifecycle.PER_CLASS)
class AllBatchExecutionHappyPathTest extends AbstractDataBatchTest {
    
    private WireMockServer wireMockServer;

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
			));
	
	final var notPermitedWords = List.of(".", "_", "png");
	
	for (final var image : images) {
	    
	   final var fileSplit1 = StringUtils.split(image.getFilename(), "_");
	   
	   final var fileId = fileSplit1[0];
	    
	   final var resultTextArray = splitByCharacterTypeCamelCase(fileSplit1[1]);
	   
	   final var resultText = Stream.of(resultTextArray)
			   .filter(s -> !notPermitedWords.contains(s))
			   .collect(joining(" "));
	   
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
	
	createBatchFile();
    }

    @AfterAll
    void afterAll() throws IOException {
	wireMockServer.stop();
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
