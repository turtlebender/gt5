package org.globus.wsrf;

import org.globus.common.PropertyHolder;

import java.lang.reflect.Method;

public class InvokeMethodRequest {
    private final Object target;
    private final Object requestObject;
    private final PropertyHolder holder;
    private final Object resourceKey;
    private final Method method;

    public InvokeMethodRequest(Object target, Object requestObject, PropertyHolder holder, Object resourceKey, Method method) {
        this.target = target;
        this.requestObject = requestObject;
        this.holder = holder;
        this.resourceKey = resourceKey;
        this.method = method;
    }

    public Object getTarget() {
        return target;
    }

    public Object getRequestObject() {
        return requestObject;
    }

    public PropertyHolder getHolder() {
        return holder;
    }

    public Object getResourceKey() {
        return resourceKey;
    }

    public Method getMethod() {
        return method;
    }
}
