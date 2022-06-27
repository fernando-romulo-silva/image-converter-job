package org.imageconverter.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BatchProcessingFileRepository extends CrudRepository<BatchProcessingFile, Long> {

    BatchProcessingFile findByName(String name);
}
