package org.globus.aop;

import java.lang.reflect.Method;

/**
 * Created by IntelliJ IDEA.
 * User: turtlebender
 * Date: Jan 20, 2009
 * Time: 4:21:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class SampleInterceptor2 extends SampleInterceptor{

    public SampleInterceptor2(String interceptorId) {
        super(interceptorId);
    }

    protected boolean before(Method target) {
        return target.getAnnotation(Sample2.class).before();
    }

    protected boolean after(Method target) {
        return target.getAnnotation(Sample2.class).after();
    }

    protected boolean afterThrows(Method target) {
        return target.getAnnotation(Sample2.class).afterThrows();
    }

    protected boolean afterFinally(Method target) {
        return target.getAnnotation(Sample2.class).afterFinally();        
    }
}
