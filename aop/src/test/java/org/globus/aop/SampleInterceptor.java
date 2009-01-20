package org.globus.aop;

import java.lang.reflect.Method;

/**
 * Created by IntelliJ IDEA.
 * User: turtlebender
 * Date: Jan 20, 2009
 * Time: 3:29:14 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class SampleInterceptor implements AOPInterceptor {
    public String interceptorId;
    boolean before = false;
    boolean after = false;
    boolean afterThrows = false;
    boolean afterFinally = false;

    public SampleInterceptor(String interceptorId) {
        this.interceptorId = interceptorId;
    }

    protected abstract boolean before(Method target);

    protected abstract boolean after(Method target);

    protected abstract boolean afterThrows(Method target);

    protected abstract boolean afterFinally(Method target);


    public void before(Method targetMethod, Object... params) {
        if (before(targetMethod))
            before = true;
    }

    public void after(Method targetMethod, Object... params) {
        if (after(targetMethod))
            after = true;
    }

    public void afterThrows(Method targetMethod, Exception e, Object... params) {
        if (afterThrows(targetMethod))
            afterThrows = true;
    }

    public void afterFinally(Method targetMethod, Object... params) {
        if (afterFinally(targetMethod))
            afterFinally = true;
    }
}
