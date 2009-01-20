package org.globus.aop;

import java.lang.reflect.Method;

/**
 * Created by IntelliJ IDEA.
 * User: turtlebender
 * Date: Jan 20, 2009
 * Time: 4:19:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class SampleInterceptor1 extends SampleInterceptor{

    public SampleInterceptor1(String interceptorId) {
        super(interceptorId);
    }

    protected boolean before(Method target) {
        return target.getAnnotation(Sample1.class).before();
    }

    protected boolean after(Method target) {
        return target.getAnnotation(Sample1.class).after();
    }

    protected boolean afterThrows(Method target) {
        return target.getAnnotation(Sample1.class).afterThrows();
    }

    protected boolean afterFinally(Method target) {
        return target.getAnnotation(Sample1.class).afterFinally();        
    }
}
