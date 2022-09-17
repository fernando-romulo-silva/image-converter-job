package org.imageconverter.infra.exception;

import java.text.MessageFormat;

public class ServerHealthDownException extends BaseApplicationException {

    private static final long serialVersionUID = 1L;
    
    public ServerHealthDownException(final String fullServerURL) {
	super(MessageFormat.format("Converter Service is \"Down\" on server url \"{0}\"", fullServerURL));
    }
}
