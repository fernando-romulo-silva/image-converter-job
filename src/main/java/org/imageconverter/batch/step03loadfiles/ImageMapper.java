package org.imageconverter.batch.step03loadfiles;

import org.imageconverter.infra.ImageFileLoad;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class ImageMapper extends BeanWrapperFieldSetMapper<ImageFileLoad> {

    @Override
    public ImageFileLoad mapFieldSet(final FieldSet fs) throws BindException {

	return new ImageFileLoad( //
			fs.readRawString("id"), //
			fs.readString("fileName"), //
			fs.readString("fileContent") //
	);
    }
}
