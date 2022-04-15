package org.imageconverter.batch.steps.step01movefiles;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

public class FolderReads implements ItemReader<Resource> {

    @Value("file:${input-files-folder}/*.txt")
    private Resource[] resources;

    @Override
    public Resource read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
	// TODO Auto-generated method stub
	return null;
    }

}
