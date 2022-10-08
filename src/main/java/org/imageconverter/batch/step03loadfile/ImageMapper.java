package org.imageconverter.batch.step03loadfile;

import org.imageconverter.infra.ImageFileLoad;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class ImageMapper extends BeanWrapperFieldSetMapper<ImageFileLoad> {

    @Override
    public ImageFileLoad mapFieldSet(final FieldSet fileSet) throws BindException {

	return new ImageFileLoad( //
			fileSet.readRawString("id"), //
			fileSet.readString("fileName"), //
			fileSet.readString("fileContent") //
	);
    }
}
