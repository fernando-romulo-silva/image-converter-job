package org.imageconverter.service;

import org.imageconverter.domain.Image;
import org.imageconverter.domain.ImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ImageService {

    private final ImageRepository repository;
    
    ImageService(final ImageRepository repository) {
	super();
	this.repository = repository;
    }

    @Transactional
    public Image save(final Image image) {
	return repository.save(image);
    }
}
