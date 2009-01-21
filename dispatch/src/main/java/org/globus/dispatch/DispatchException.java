package org.globus.dispatch;

/**
 * Created by IntelliJ IDEA.
 * User: turtlebender
 * Date: Jan 12, 2009
 * Time: 7:56:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class DispatchException extends Exception{
    public DispatchException() {
    }

    public DispatchException(String s) {
        super(s);
    }

    public DispatchException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public DispatchException(Throwable throwable) {
        super(throwable);
    }
}
