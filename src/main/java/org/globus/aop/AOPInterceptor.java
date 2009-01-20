package org.globus.wsrf.impl.resources;

import java.lang.reflect.Method;

/**
 * Created by IntelliJ IDEA.
 * User: turtlebender
 * Date: Jan 20, 2009
 * Time: 12:48:51 PM
 * To change this template use File | Settings | File Templates.
 */
public interface AOPInterceptor {

    public void before(Method targetMethod, Object... params);

    public void after(Method targetMethod, Object... params);

    public void afterThrows(Method targetMethod, Exception e, Object... params);

    public void afterFinally(Method targetMethod, Object... params);
}

