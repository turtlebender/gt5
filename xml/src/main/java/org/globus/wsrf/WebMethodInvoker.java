package org.globus.wsrf;

import org.globus.common.PropertyHolder;
import org.springframework.oxm.GenericMarshaller;
import org.springframework.oxm.GenericUnmarshaller;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.w3c.dom.Node;

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class WebMethodInvoker {

    private XPathEvaluator xpression;
    private Unmarshaller unmarshaller;
    private Marshaller marshaller;
    private ResourcefulMethodInvoker rmi;

    public ResourcefulMethodInvoker getRmi() {
        return rmi;
    }

    public void setRmi(ResourcefulMethodInvoker rmi) {
        this.rmi = rmi;
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

    public Object invoke(Method method, Object target, Object requestObject, Source headerSource,
                         PropertyHolder holder) throws Exception {
        Object resourceKey = getResourceKey(headerSource);
        return rmi.invokeMethod(new InvokeMethodRequest(target, requestObject, holder, resourceKey, method));
    }

    private Object getResourceKey(Source headerSource) throws Exception {
        Node results = this.xpression.evaluate(headerSource);
        if (results != null) {
            return unmarshaller.unmarshal(new DOMSource(results));
        } else {
            return null;
        }
    }

    public boolean supports(Method method) {
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
