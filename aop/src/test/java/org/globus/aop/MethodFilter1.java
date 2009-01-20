package org.globus.aop;

import java.lang.reflect.Method;

/**
 * Created by IntelliJ IDEA.
 * User: turtlebender
 * Date: Jan 20, 2009
 * Time: 3:38:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class MethodFilter1 implements MethodFilter{
    public boolean accept(Method method) {
        return method.getAnnotation(Sample1.class) != null;
    }
}
