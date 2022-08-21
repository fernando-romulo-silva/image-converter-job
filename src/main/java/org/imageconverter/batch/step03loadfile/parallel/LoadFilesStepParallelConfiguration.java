package org.imageconverter.batch.step03loadfile.parallel;

import static java.io.File.separator;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.imageconverter.config.BatchConfiguration.LOAD_FILE_STEP_PARALELL;

import java.io.IOException;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.imageconverter.domain.Image;
import org.imageconverter.infra.ImageFileLoad;
import org.imageconverter.util.DefaultStepListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class LoadFilesStepParallelConfiguration {

    private static final String LOAD_FILES_STEP_PARALELL_SLAVE = "loadFilesStepParalellSlave";

    private final StepBuilderFactory stepBuilderFactory;

    LoadFilesStepParallelConfiguration(final StepBuilderFactory stepBuilderFactory) {
	super();
	this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    Step loadFilesStepParalell( //
		    final ItemReader<ImageFileLoad> paralellItemReader, //
		    final ItemProcessor<ImageFileLoad, Image> loadFileProcessor, //
		    final ItemWriter<Image> loadFileWriter, //
		    final PlatformTransactionManager transactionManager,
		    //
		    @Value("${application.chunk-size}") //
		    final Integer chunkSize, //
		    final Partitioner partitioner, //
		    final DefaultStepListener defaultStepListener,
		    final ThreadPoolTaskExecutor taskExecutor) {

	final var loadFilesStepParalellSlave = this.stepBuilderFactory //
			.get(LOAD_FILES_STEP_PARALELL_SLAVE) //
			.listener(defaultStepListener) //
			//
			.transactionManager(transactionManager) //
			.<ImageFileLoad, Image>chunk(chunkSize) //
			//
			.reader(paralellItemReader) //
			.processor(loadFileProcessor) //
			.writer(loadFileWriter) //
			//
			.build();

	return this.stepBuilderFactory //
			.get(LOAD_FILE_STEP_PARALELL) //
			.partitioner(LOAD_FILES_STEP_PARALELL_SLAVE, partitioner) //
			.step(loadFilesStepParalellSlave) //
			.taskExecutor(taskExecutor) //
			.build();
    }

    @Bean
    ThreadPoolTaskExecutor taskExecutor() {
	final var taskExecutor = new ThreadPoolTaskExecutor();
	taskExecutor.setThreadGroupName("parallelThreadPoolTaskExecutor");
	taskExecutor.setMaxPoolSize(10);
	taskExecutor.setCorePoolSize(10);
	taskExecutor.setQueueCapacity(10);
	taskExecutor.afterPropertiesSet();
	return taskExecutor;
    }

    @Bean
    @StepScope
    Partitioner partitioner(//
		    @Value("#{jobParameters['fileName']}") //
		    final String fileName, //
		    //
		    @Value("${application.batch-folders.processing-files}") //
		    final Resource processingFolder //

    ) throws IOException {

	final var baseName = FilenameUtils.getBaseName(fileName);

	final var partitioner = new MultiResourcePartitioner();
	final var resolver = new PathMatchingResourcePatternResolver();

	final var filesFolder = resolver.getResources(processingFolder.getURI() + separator + baseName + "*.txt");

	final var filesList = Stream.of(filesFolder) //
			.filter(r -> !equalsIgnoreCase(r.getFilename(), fileName)) //
			.toList();

	final var resources = filesList.toArray(new Resource[filesList.size()]);

	partitioner.setResources(resources);
	partitioner.partition(10);
	return partitioner;
    }
}
