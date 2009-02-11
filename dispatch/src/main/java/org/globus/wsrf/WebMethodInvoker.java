package org.globus.wsrf;

import org.globus.common.MethodInvocationInterceptor;
import org.globus.common.PropertyHolder;
import org.springframework.oxm.GenericMarshaller;
import org.springframework.oxm.GenericUnmarshaller;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.ws.server.endpoint.MethodEndpoint;
import org.w3c.dom.Node;

import javax.xml.bind.JAXBElement;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

public class WebMethodInvoker {

    private XPathEvaluator xpression;
    private Unmarshaller unmarshaller;
    private Marshaller marshaller;
    private List<MethodInvocationInterceptor> interceptors;


    public List<MethodInvocationInterceptor> getInterceptors() {
        return interceptors;
    }

    public void setInterceptors(List<MethodInvocationInterceptor> interceptors) {
        this.interceptors = interceptors;
    }

    public Marshaller getMarshaller() {
        return marshaller;
    }

    public void setMarshaller(Marshaller marshaller) {
        this.marshaller = marshaller;
    }

    public Unmarshaller getUnmarshaller() {
        return unmarshaller;
    }

    public void setUnmarshaller(Unmarshaller unmarshaller) {
        this.unmarshaller = unmarshaller;
    }

    public XPathEvaluator getXpression() {
        return xpression;
    }

    public void setXpression(XPathEvaluator xpression) {
        this.xpression = xpression;
    }

    public Object invoke(MethodEndpoint methodEndpoint, Object requestObject, Source headerSource,
                         PropertyHolder holder) throws Exception {
        Method method = methodEndpoint.getMethod();
        int numParams = method.getParameterTypes().length;
        Object[] params = new Object[numParams];
        if (params.length > 1) {
            int resourceIndex = getResourcefulIndex(method);
            switch (resourceIndex) {
                case 0:
                    params[0] = getResourceKey(headerSource);
                    params[1] = requestObject;
                    break;
                case 1:
                    params[0] = requestObject;
                    params[1] = getResourceKey(headerSource);
                    break;

            }
        } else if (params.length == 1) {
            int resourceIndex = getResourcefulIndex(method);
            if (resourceIndex == 0) {
                params[0] = getResourceKey(headerSource);
            } else {
                params[0] = requestObject;
            }
        }
        if (this.interceptors != null) {
            for (MethodInvocationInterceptor interceptor : this.interceptors) {
                interceptor.intercept(holder, method, params);
            }
        }
        return methodEndpoint.invoke(params);
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

    private Object getResourceKey(Source headerSource) throws Exception {
        Node results = this.xpression.evaluate(headerSource);
        Object key = unmarshaller.unmarshal(new DOMSource(results));
        if (key instanceof JAXBElement) {
            key = ((JAXBElement) key).getValue();
        }
        return key;
    }

    public boolean supports(MethodEndpoint methodEndpoint) {
        Method method = methodEndpoint.getMethod();
        return supportsReturnType(method) && supportsParameters(method);
    }

    private boolean supportsReturnType(Method method) {
        if (Void.TYPE.equals(method.getReturnType())) {
            return true;
        } else {
            if (getMarshaller() instanceof GenericMarshaller) {
                GenericMarshaller marshaller = (GenericMarshaller) getMarshaller();
                Type type = method.getGenericReturnType();
                return marshaller.supports(type);
            } else {
                return getMarshaller().supports(method.getReturnType());
            }
        }
    }

    private boolean supportsParameters(Method method) {
        if (method.getParameterTypes().length > 2) {
            return false;
        }
        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < method.getParameterTypes().length; i++) {
            if (paramAnnotations[i] != null && paramAnnotations.length > 0) {
                boolean resourceful = false;
                for (Annotation annotation : paramAnnotations[i]) {
                    if (annotation instanceof Resourceful) {
                        resourceful = true;
                    }
                }
                if (resourceful)
                    continue;
            }
            if (getUnmarshaller() instanceof GenericUnmarshaller) {
                GenericUnmarshaller genericUnmarshaller = (GenericUnmarshaller) getUnmarshaller();
                if (!genericUnmarshaller.supports(method.getGenericParameterTypes()[i])) {
                    return false;
                }
            } else {
                if (!getUnmarshaller().supports(method.getParameterTypes()[i])) {
                    return false;
                }
            }
        }
        return true;
    }

}
