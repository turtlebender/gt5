package org.globus.aop;

import java.lang.reflect.Method;

/**
 * This interface should determine which methods need to be enhanced by a particular aspect (more commonly known as a
 * joinpoint).  One common method of using this is to check if the method has a specific annotation applied and use that
 * info to determine whether the aspect applies.
 *
 * @author Tom Howe
 */
public interface MethodFilter {

    /**
     * Should the aspect be applied to this method.
     *
     * @param method The method in question
     * @return Should the aspect be applied.
     */
    public boolean accept(Method method);
}
