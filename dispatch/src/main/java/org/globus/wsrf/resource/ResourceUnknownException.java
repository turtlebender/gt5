package org.globus.wsrf.resource;

/**
 * Created by IntelliJ IDEA.
 * User: turtlebender
 * Date: Feb 10, 2009
 * Time: 9:38:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class ResourceUnknownException extends ResourceException{
    public ResourceUnknownException() {
    }

    public ResourceUnknownException(String message) {
        super(message);
    }

    public ResourceUnknownException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceUnknownException(Throwable cause) {
        super(cause);
    }
}
