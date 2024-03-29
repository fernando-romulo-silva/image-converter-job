package org.imageconverter.batch.step03loadfile;

import org.imageconverter.infra.ImageFileLoad;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;

@StepScope
@Component
public class LoadFileSetMapper implements FieldSetMapper<ImageFileLoad> {

    @Override
    public ImageFileLoad mapFieldSet(final FieldSet fieldSet) throws BindException {

	return new ImageFileLoad( //
			fieldSet.readString(0), //
			fieldSet.readString(1), //
			fieldSet.readString(2));
    }
}
