package org.imageconverter.batch.steps.step01movefile;

import java.io.File;
import java.util.Arrays;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
@StepScope
public class MoveFileStep implements Tasklet {   

    @Value("file:${input-files-folder}/*.txt")
    private Resource[] resources;
    
    @Value("#{jobParameters['file']}") 
    private File file;
    
    @Value("#{jobParameters['fileName']}")
    private 

    @Override
    public RepeatStatus execute(final StepContribution stepContribution, final ChunkContext chunkContext) throws Exception {    

        final File directory = new File("");
        
        Arrays.asList(directory.listFiles((dir, name) -> name.matches("yourfilePrefix.*?")))
                .stream()
                .forEach(singleFile -> singleFile.renameTo(new File("someNewFilePath")));               
        
        return RepeatStatus.FINISHED;

    }

}
