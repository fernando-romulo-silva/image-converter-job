package org.imageconverter.domain;

import static javax.persistence.GenerationType.IDENTITY;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "BATCH_PROCESSING_FILE", uniqueConstraints = @UniqueConstraint(columnNames = "BPF_NAME", name = "UK_BPF_NAME"))
public class BatchProcessingFile {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "BPF_ID", nullable = false)
    private Long id;

    @NotEmpty(message = "The 'name' cannot be empty")
    @Column(name = "BPF_NAME", nullable = false, unique = true)
    private String name;

    @Column(name = "BPF_CREATED", nullable = false)
    private LocalDateTime created;

    @Column(name = "BPF_FINISHED", nullable = false)
    private LocalDateTime finished;

    BatchProcessingFile() {
	super();
    }

    public BatchProcessingFile(final String name) {
	super();
	this.name = name;
	this.created = LocalDateTime.now();
    }

    public Long getId() {
	return id;
    }

    public String getName() {
	return name;
    }

    public LocalDateTime getCreated() {
	return created;
    }

    public Optional<LocalDateTime> getFinished() {
	return Optional.ofNullable(finished);
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

	if (obj instanceof BatchProcessingFile other) {
	    return Objects.equals(id, other.id);
	}

	return false;
    }

    @Override
    public String toString() {
	final var builder = new StringBuilder();

	builder.append("BatchProcessingFile [id=").append(id) //
			.append(", name=").append(name) //
			.append(", created=").append(created) //
			.append(", finished=").append(finished) //
			.append("]");
	return builder.toString();
    }
}
