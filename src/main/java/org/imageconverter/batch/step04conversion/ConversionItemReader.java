package org.imageconverter.batch.step04conversion;

import java.util.Collections;

import javax.persistence.EntityManagerFactory;

import org.imageconverter.domain.Image;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.orm.JpaNativeQueryProvider;
import org.springframework.stereotype.Component;

@StepScope
@Component
public class ConversionItemReader extends JpaPagingItemReader<Image> {

    private final EntityManagerFactory entityManagerFactory;
    
    ConversionItemReader(final EntityManagerFactory entityManagerFactory) {
	super();
	this.entityManagerFactory = entityManagerFactory;
	
	config();
    }
    
    
    private void config() {
	
	final var sql = "select * from IMAGE_BATCH where IMG_ID >= :limit";
        
	final var queryProvider = new JpaNativeQueryProvider<Image>();
        queryProvider.setSqlQuery(sql);
        queryProvider.setEntityClass(Image.class);
        
        setQueryProvider(queryProvider);
        
        setParameterValues(Collections.singletonMap("limit", 10));
        setEntityManagerFactory(entityManagerFactory);
        setPageSize(3);
        setSaveState(true);
    }
}    
