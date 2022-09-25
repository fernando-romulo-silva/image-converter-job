package org.imageconverter.infra;

import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.FileNotFoundException;

import org.imageconverter.infra.exception.ImageConvertException;
import org.slf4j.Logger;
import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.stereotype.Component;

@Component
public class BatchSkipPolicy implements SkipPolicy {

    private static final Logger LOGGER = getLogger(BatchSkipPolicy.class);

    @Override
    public boolean shouldSkip(final Throwable exception, int skipCount) throws SkipLimitExceededException {

	final var errorMessage = new StringBuilder();

	if (exception instanceof ImageConvertException ice) {

	    LOGGER.warn(errorMessage.toString(), getRootCauseMessage(exception));

	    return true;

	} else if (exception instanceof FileNotFoundException) {

	    LOGGER.error(errorMessage.toString(), getRootCauseMessage(exception));

	    return false;

	} else if (exception instanceof FlatFileParseException ffp) {

	    final var msg = "Unexpected end of file before record complete";

	    if (containsIgnoreCase(getRootCauseMessage(exception), msg)) {

		return true; // problems with the end of file, that's ok
	    }

	    errorMessage.append("An error occured while processing the ") //
			    .append(ffp.getLineNumber()) //
			    .append(" line of the file. The faulty is ") //
			    .append(ffp.getMessage()) //
			    .append(". Qty ").append(skipCount);

	    LOGGER.error(errorMessage.toString(), ffp);

	    return true;
	}

	return false;
    }

}
