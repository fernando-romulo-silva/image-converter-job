package org.imageconverter.infra.exception;

public class ConversionErrorException extends BaseApplicationException {

    private static final long serialVersionUID = 1L;

    public ConversionErrorException(final String msg) {
	super(msg);
    }
}
