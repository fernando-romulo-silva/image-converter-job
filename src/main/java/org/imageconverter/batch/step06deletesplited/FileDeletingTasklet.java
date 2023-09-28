package org.imageconverter.batch.step06deletesplited;

import java.nio.file.Files;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.UnexpectedJobExecutionException;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class FileDeletingTasklet implements Tasklet, InitializingBean {

    private Resource[] resources;

    public RepeatStatus execute(final StepContribution contribution, final ChunkContext chunkContext) throws Exception {

	for (final var resource : resources) {
	    final var file = resource.getFile();

	    final boolean deleted = Files.deleteIfExists(file.toPath());

	    if (!deleted) {
		throw new UnexpectedJobExecutionException("Could not delete file " + file.getPath());
	    }
	}

	return RepeatStatus.FINISHED;
    }

    public void setResources(final Resource[] resources) {
	this.resources = resources;
    }

    public void afterPropertiesSet() throws Exception {
	Assert.notNull(resources, "directory must be set");
    }
}
