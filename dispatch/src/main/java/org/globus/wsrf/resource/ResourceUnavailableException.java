package org.globus.wsrf.resource;


public class ResourceUnavailableException extends ResourceException{

    public ResourceUnavailableException() {
    }

    public ResourceUnavailableException(String message) {
        super(message);
    }

    public ResourceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceUnavailableException(Throwable cause) {
        super(cause);
    }
}
