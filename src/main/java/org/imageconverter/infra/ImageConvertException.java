package org.imageconverter.infra;

public class ImageConvertException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ImageConvertException(final String message) {
	super(message);
    }

    public ImageConvertException(final String message, final Throwable cause) {
	super(message, cause);
    }
}
