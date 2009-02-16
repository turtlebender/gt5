package org.globus.dispatch.camel;

import java.lang.reflect.Method;

/**
 * Created by IntelliJ IDEA.
 * User: turtlebender
 * Date: Feb 13, 2009
 * Time: 9:50:29 AM
 * To change this template use File | Settings | File Templates.
 */
public class CamelMethodEndpoint {
    private Method targetMethod;
    private Object target;

    public CamelMethodEndpoint(Method targetMethod, Object target) {
        setTargetMethod(targetMethod);
        setTarget(target);
    }

    public CamelMethodEndpoint() {
    }

    public Method getTargetMethod() {
        return targetMethod;
    }

    public void setTargetMethod(Method targetMethod) {
        this.targetMethod = targetMethod;
        this.targetMethod.setAccessible(true);
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }
}
