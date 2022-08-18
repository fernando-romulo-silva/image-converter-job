package org.imageconverter.batch.step04conversion;

import static java.io.File.separator;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import org.imageconverter.batch.step03loadfile.LoadFileSetMapper;
import org.imageconverter.domain.Image;
import org.imageconverter.infra.ImageFileLoad;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.AbstractLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;

@StepScope
@Component
public class ConversionItemReader extends JpaPagingItemReader<Image> {

    ConversionItemReader( //

    )  {

    }
}
