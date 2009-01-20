package org.globus.aop;

import java.lang.reflect.Method;

/**
 * This interface determines what actions occur when the aspect is applied.  None of the method is required to implement
 * more than a Null Operation.
 *
 * @author Tom Howe
 */
public interface AOPInterceptor {

    /**
     * Will execute before the method is invoked.
     *
     * @param targetMethod The method
     * @param params The method Params
     */
    public void before(Method targetMethod, Object... params);

    /**
     * Will execute after the method is invoked.
     *
     * @param targetMethod The method
     * @param params The method Params
     */
    public void after(Method targetMethod, Object... params);

    /**
     * Will execute after any exception is thrown.
     *
     * @param targetMethod The method
     * @param params The method Params
     */
    public void afterThrows(Method targetMethod, Exception e, Object... params);

    /**
     * Will execute after any finally blocks from the method.
     *
     * @param targetMethod The method
     * @param params The method Params
     */public void afterFinally(Method targetMethod, Object... params);
}

