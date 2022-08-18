package org.imageconverter.infra.exception;

public class UnexpectedConversionException extends BaseApplicationException {

    private static final long serialVersionUID = 1L;

    public UnexpectedConversionException(final String msg) {
	super(msg);
    }
}
