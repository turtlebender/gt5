package org.globus.dispatch.binding;

/**
 * Created by IntelliJ IDEA.
 * User: turtlebender
 * Date: Jan 12, 2009
 * Time: 7:50:20 PM
 * To change this template use File | Settings | File Templates.
 */
public interface DispatchBinding {

    public Object dispatch(Object... params);
}
