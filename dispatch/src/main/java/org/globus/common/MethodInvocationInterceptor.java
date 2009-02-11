package org.globus.common;

import java.lang.reflect.Method;


public interface MethodInvocationInterceptor {
    public void intercept(PropertyHolder propertyHolder, Method targetMethod,
                             Object... params);
}
