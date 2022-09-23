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
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "IMAGE_BATCH")
public class Image {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "IMG_ID", nullable = false)
    private String id;

    @Column(name = "IMG_NAME", nullable = false, unique = true)
    private String name;

    @Column(name = "IMG_BATCH_FILE" )
    private String file;

    @Column(name = "IMG_CREATED", nullable = false)
    private LocalDateTime created;

    @Column(name = "IMG_CONVERSION")
    private String conversion;
    
    Image() {
	super();
    }

    public Image( //
		    @NotEmpty(message = "The 'name' cannot be empty")//
		    final String name, // 
		    //
		    @NotNull(message = "The 'file' cannot be null")
		    final String file) {
	super();
	this.name = name;
	this.file = file;
    }
    
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getFile() {
        return file;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public String getConversion() {
        return conversion;
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
