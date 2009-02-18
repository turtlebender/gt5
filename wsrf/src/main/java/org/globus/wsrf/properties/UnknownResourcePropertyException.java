package org.globus.wsrf.properties;

import org.globus.wsrf.ResourceException;

/**
 * Created by IntelliJ IDEA.
 * User: turtlebender
 * Date: Feb 17, 2009
 * Time: 12:50:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class UnknownResourcePropertyException extends ResourceException{
    public UnknownResourcePropertyException() {
    }

    public UnknownResourcePropertyException(String message) {
        super(message);
    }

    public UnknownResourcePropertyException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownResourcePropertyException(Throwable cause) {
        super(cause);
    }
}
