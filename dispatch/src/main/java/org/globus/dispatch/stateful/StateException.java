package org.globus.dispatch.stateful;

/**
 * Created by IntelliJ IDEA.
 * User: turtlebender
 * Date: Jan 22, 2009
 * Time: 10:30:21 AM
 * To change this template use File | Settings | File Templates.
 */
public class StateException extends Exception{
    public StateException() {
    }

    public StateException(String s) {
        super(s);
    }

    public StateException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public StateException(Throwable throwable) {
        super(throwable);
    }
}
