package org.imageconverter.batch.step03loadfile;

import org.imageconverter.infra.BatchSkipPolicy;
import org.imageconverter.util.RecordSepartatorPolicy;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.file.separator.SimpleRecordSeparatorPolicy;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadFilesStepConfiguration {
    
    @Bean
    SkipPolicy fileVerificationSkipper() {
	return new BatchSkipPolicy();
    }

    @Bean
    SimpleRecordSeparatorPolicy blankLineRecordSeparatorPolicy() {
	return new RecordSepartatorPolicy();
    }

    @Bean
    FixedLengthTokenizer imageFileFixedLength() {
	final var tokenizer = new FixedLengthTokenizer();

	tokenizer.setNames( //
			"id", //
			"fileName", //
			"image" //
	);

	tokenizer.setColumns( //
			new Range(1, 10), //
			new Range(12, 22), //
			new Range(24, 1000) //
	);

	tokenizer.setStrict(false);

	return tokenizer;
    }

    @Bean
    DelimitedLineTokenizer imageFileDelimitedTokenizer() {
	final var tokenizer = new DelimitedLineTokenizer(";");

	tokenizer.setNames( //
			"id", //
			"fileName", //
			"image" //
	);

	tokenizer.setStrict(false);

	return tokenizer;
    }
}
