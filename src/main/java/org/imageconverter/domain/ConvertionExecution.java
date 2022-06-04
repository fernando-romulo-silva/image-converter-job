package org.imageconverter.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class ConvertionExecution {

    @Id
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "IMAGE_ID", nullable = false, updatable = false)
    private Image image;

}
