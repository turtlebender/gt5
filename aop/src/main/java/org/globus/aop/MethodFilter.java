package org.globus.wsrf.impl.resources;

import java.lang.reflect.Method;

/**
 * Created by IntelliJ IDEA.
 * User: turtlebender
 * Date: Jan 20, 2009
 * Time: 1:46:13 PM
 * To change this template use File | Settings | File Templates.
 */
public interface MethodFilter {

    public boolean accept(Method method);
}
