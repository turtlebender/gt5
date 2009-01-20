package org.globus.aop;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: turtlebender
 * Date: Jan 20, 2009
 * Time: 3:29:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class SampleInterceptor implements AOPInterceptor{
    public Map<Method, Integer> beforeMap = new HashMap<Method, Integer>();

    public void before(Method targetMethod, Object... params) {

    }

    public void after(Method targetMethod, Object... params) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void afterThrows(Method targetMethod, Exception e, Object... params) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void afterFinally(Method targetMethod, Object... params) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
