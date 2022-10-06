package org.imageconverter.batch;

import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomUtils;
import org.imageconverter.domain.Image;
import org.imageconverter.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class AbstractDataBatchTest extends AbstractBatchTest {

    @Qualifier("batchDataSource")
    @Autowired
    protected DataSource batchDataSource;

    @Autowired
    protected EntityManager entityManager;
    
    @Autowired
    protected ImageService imageService;
    
    protected List<Entry<String, String>> createBatchDb() throws IOException {

	var i = 1L;

	final var result = new ArrayList<Entry<String, String>>();
	final var jobId = RandomUtils.nextLong();
	
	for (final var resource : images) {

	    final var file = resource.getFile();

	    final var fileContent = FileUtils.readFileToByteArray(file);

	    final var imageFileId = i;
	    final var imageFileName = file.getName();
	    final var imageEncodedString = Base64.getEncoder().encodeToString(fileContent);

	    final var image = new Image(imageFileName, fileName, imageEncodedString, jobId);
	    	    
	    result.add(new SimpleEntry<>(String.valueOf(imageFileId), imageFileName));

	    imageService.save(image);
	    
	    i++;
	}
	
	return result;
    }
    
}
