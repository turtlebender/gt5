package org.globus.wsrf;

import org.globus.common.MethodInvocationInterceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

public class ResourcefulMethodInvoker {

    private List<MethodInvocationInterceptor> interceptors;


    public List<MethodInvocationInterceptor> getInterceptors() {
        return interceptors;
    }

    public void setInterceptors(List<MethodInvocationInterceptor> interceptors) {
        this.interceptors = interceptors;
    }


    protected int getResourcefulIndex(Method method) {
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
            invokeMultipleArgs(invokeMethodRequest, params);
        } else if (params.length == 1) {
            invokeSingleArg(invokeMethodRequest, params);
        }
        addInterceptors(invokeMethodRequest, params);
        return invokeMethodRequest.getMethod().invoke(invokeMethodRequest.getTarget(), params);
    }

    private void addInterceptors(InvokeMethodRequest invokeMethodRequest, Object[] params) {
        if (this.interceptors != null) {
            for (MethodInvocationInterceptor interceptor : this.interceptors) {
                interceptor.intercept(invokeMethodRequest.getHolder(), invokeMethodRequest.getMethod(), params);
            }
        }
    }

    private void invokeSingleArg(InvokeMethodRequest invokeMethodRequest, Object[] params) {
        int resourceIndex = getResourcefulIndex(invokeMethodRequest.getMethod());
        if (resourceIndex == 0) {
            params[0] = invokeMethodRequest.getResourceKey();
        } else {
            params[0] = invokeMethodRequest.getRequestObject();
        }
    }

    private void invokeMultipleArgs(InvokeMethodRequest invokeMethodRequest, Object[] params) {
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
    }
}
