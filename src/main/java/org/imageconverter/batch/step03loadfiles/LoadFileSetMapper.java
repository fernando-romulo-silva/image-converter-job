package org.imageconverter.batch.step03loadfiles;

import org.imageconverter.domain.ImageFileLoad;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;

@Component
public class LoadFileSetMapper implements FieldSetMapper<ImageFileLoad> {

    @Override
    public ImageFileLoad mapFieldSet(final FieldSet fieldSet) throws BindException {

	final var imageFileLoad = new ImageFileLoad( //
			fieldSet.readString(0), //
			fieldSet.readString(1), //
			fieldSet.readString(2));

	return imageFileLoad;
    }
}
