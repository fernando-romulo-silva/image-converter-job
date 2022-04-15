package org.imageconverter.util;

import static org.apache.commons.lang3.StringUtils.isBlank;

import org.springframework.batch.item.file.separator.SimpleRecordSeparatorPolicy;

public class RecordSepartatorPolicy extends SimpleRecordSeparatorPolicy {

    @Override
    public boolean isEndOfRecord(final String line) {
	return line.trim().length() != 0 && super.isEndOfRecord(line);
    }

    @Override
    public String postProcess(final String record) {

	if (isBlank(record)) {
	    return null;
	}

	return super.postProcess(record);
    }
}
