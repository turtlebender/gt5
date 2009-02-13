package org.globus.wsrf;

import org.globus.common.MethodInvocationInterceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: turtlebender
 * Date: Feb 12, 2009
 * Time: 8:46:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class ResourcefulMethodInvoker {

    private List<MethodInvocationInterceptor> interceptors;


    public List<MethodInvocationInterceptor> getInterceptors() {
        return interceptors;
    }

    public void setInterceptors(List<MethodInvocationInterceptor> interceptors) {
        this.interceptors = interceptors;
    }


    private int getResourcefulIndex(Method method) {
        Annotation[][] annotations = method.getParameterAnnotations();
        for (int i = 0; i < method.getParameterTypes().length; i++) {
            for (Annotation annotation : annotations[i]) {
                if (annotation instanceof Resourceful) {
                    return i;
                }
            }
        }
        return -1;
    }

    public Object invokeMethod(InvokeMethodRequest invokeMethodRequest) throws Exception {
        int numParams = invokeMethodRequest.getMethod().getParameterTypes().length;
        Object[] params = new Object[numParams];
        if (params.length > 1) {
            int resourceIndex = getResourcefulIndex(invokeMethodRequest.getMethod());
            switch (resourceIndex) {
                case 0:
                    params[0] = invokeMethodRequest.getResourceKey();
                    params[1] = invokeMethodRequest.getRequestObject();
                    break;
                case 1:
                    params[0] = invokeMethodRequest.getRequestObject();
                    params[1] = invokeMethodRequest.getResourceKey();
                    break;

            }
        } else if (params.length == 1) {
            int resourceIndex = getResourcefulIndex(invokeMethodRequest.getMethod());
            if (resourceIndex == 0) {
                params[0] = invokeMethodRequest.getResourceKey();
            } else {
                params[0] = invokeMethodRequest.getRequestObject();
            }
        }
        if (this.interceptors != null) {
            for (MethodInvocationInterceptor interceptor : this.interceptors) {
                interceptor.intercept(invokeMethodRequest.getHolder(), invokeMethodRequest.getMethod(), params);
            }
        }
        return invokeMethodRequest.getMethod().invoke(invokeMethodRequest.getTarget(), params);
    }    
}
