package org.imageconverter.domain;

import static javax.persistence.GenerationType.IDENTITY;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "IMAGE_BATCH")
public class Image {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "IMG_ID", nullable = false)
    private Long id;

    @Column(name = "IMG_NAME", nullable = false, unique = true)
    private String name;

    @Column(name = "IMG_CONTENT")
    private String content;
    
    @Column(name = "IMG_BATCH_FILE")
    private String batchFile;

    @Column(name = "IMG_CREATED", nullable = false)
    private LocalDateTime created;
    
    @Column(name = "IMG_UPDATED", nullable = true)
    private LocalDateTime updated;

    @Column(name = "IMG_CONVERSION")
    private String conversion;
    
    Image() {
	super();
    }

    public Image( //
		    @NotEmpty(message = "The 'name' cannot be empty")//
		    final String name, // 
		    //
		    @NotEmpty(message = "The 'batchFile' cannot be empty")
		    final String batchFile,
		    //
		    @NotEmpty(message = "The 'content' cannot be empty")
		    final String content
		    
		    ) {
	super();
	this.name = name;
	this.batchFile = batchFile;
	this.content = content;
	this.created = LocalDateTime.now();
    }
    
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBatchFile() {
        return batchFile;
    }

    public LocalDateTime getCreated() {
        return created;
    }
    
    public String getContent() {
	return content;
    }

//    public String getConversion() {
//        return conversion;
//    }
    
    public void updateConvertion(final String text) {
	conversion = text;
	updated = LocalDateTime.now();
    }

    @Override
    public int hashCode() {
	return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {

	if (this == obj) {
	    return true;
	}

	if (obj instanceof Image other) {
	    return Objects.equals(id, other.id);
	}

	return false;
    }

    @Override
    public String toString() {
	final var builder = new StringBuilder();

	builder.append("Image [id=").append(id) //
			.append(", name=").append(name) //
			.append(", created=").append(created) //
			.append(", conversion=").append(conversion) //
			.append("]");

	return builder.toString();
    }
}
