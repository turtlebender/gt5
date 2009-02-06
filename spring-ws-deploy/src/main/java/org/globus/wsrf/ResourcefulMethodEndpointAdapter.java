package org.globus.wsrf;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.globus.wsrf.annotations.StatefulResource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.oxm.GenericMarshaller;
import org.springframework.oxm.GenericUnmarshaller;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.MethodEndpoint;
import org.springframework.ws.server.endpoint.adapter.GenericMarshallingMethodEndpointAdapter;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.support.MarshallingUtils;
import org.springframework.xml.xpath.Jaxp13XPathTemplate;
import org.w3c.dom.Node;

import javax.xml.bind.JAXBElement;
import javax.xml.transform.dom.DOMSource;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class ResourcefulMethodEndpointAdapter
        extends GenericMarshallingMethodEndpointAdapter implements BeanPostProcessor {
    private Jaxp13XPathTemplate xpathTemplate;
    private String localName;
    private String namespace;

    class DelegatingHandler implements MethodInterceptor {
        List<Class> interfaces;
        List<Object> delegates;
        Object parent;

        public DelegatingHandler(Object parent, List<Class> interfaces, List<Object> delegates) {
            this.interfaces = interfaces;
            this.delegates = delegates;
            this.parent = parent;
        }

        public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            Class declaringClass = method.getDeclaringClass();
            for (int i = 0; i < interfaces.size(); i++) {
                if (declaringClass.isAssignableFrom(interfaces.get(i))) {
                    try {
                        return methodProxy.invoke(delegates.get(i), args);
                    } catch (InvocationTargetException e) {
                        throw e.getTargetException();
                    }
                }
            }
            return methodProxy.invoke(parent, args);
        }
    }


    private DefaultGetRPProviderFactory fac = new DefaultGetRPProviderFactory();

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (localName == null) {
            StatefulResource sr = bean.getClass().getAnnotation(StatefulResource.class);
            if (sr != null) {
                this.localName = sr.keyLocalpart();
                this.namespace = sr.keyNamespace();                
                List<Object> delegate = new ArrayList<Object>();
                List<Class> interfaces = new ArrayList<Class>();
                //TODO: Obviously this is a hack, we need to have these set up a bit more dynamically.
                if (fac.supports(bean)) {
                    delegate.add(fac.getDelegate(bean));
                    interfaces.add(fac.getInterface());
                }
                DelegatingHandler handler = new DelegatingHandler(bean, interfaces, delegate);
                Enhancer enhancer = new Enhancer();
                enhancer.setSuperclass(bean.getClass());
                enhancer.setInterfaces(interfaces.toArray(new Class[interfaces.size()]));
                enhancer.setCallback(handler);
                return enhancer.create();
            }
        }
        xpathTemplate = new Jaxp13XPathTemplate();
        Properties props = new Properties();
        props.put("user", this.namespace);
        xpathTemplate.setNamespaces(props);
        //TODO: This is pretty hackish, but it works.  Probably do it with a factory
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        return bean;
    }


    private Object getResourceKey(MessageContext messageContext) throws Exception {
        SoapMessage request = (SoapMessage) messageContext.getRequest();
        String expression = String.format("//user:%s", this.localName);
        Node results = this.xpathTemplate.evaluateAsNode(expression, request.getSoapHeader().getSource());
        Object key = getUnmarshaller().unmarshal(new DOMSource(results));
        if (key instanceof JAXBElement) {
            key = ((JAXBElement) key).getValue();
        }
        return key;
    }

    protected void invokeInternal(MessageContext messageContext, MethodEndpoint methodEndpoint) throws Exception {
        try {
            SoapMessage request = (SoapMessage) messageContext.getRequest();
            Object requestObject = unmarshalRequest(request);
            int numParams = methodEndpoint.getMethod().getParameterTypes().length;
            Object[] params = new Object[numParams];
            int resourceIndex = getResourcefulIndex(methodEndpoint.getMethod());
            switch (resourceIndex) {
                case 0:
                    params[0] = getResourceKey(messageContext);
                    params[1] = requestObject;
                    break;
                case 1:
                    params[0] = requestObject;
                    params[1] = getResourceKey(messageContext);
                    break;
                default:
                    params[0] = requestObject;
            }
            Object responseObject = methodEndpoint.invoke(params);
            if (responseObject != null) {
                WebServiceMessage response = messageContext.getResponse();
                marshalResponse(responseObject, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
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

    private Object unmarshalRequest(WebServiceMessage request) throws IOException {
        Object requestObject = MarshallingUtils.unmarshal(getUnmarshaller(), request);
        if (logger.isDebugEnabled()) {
            logger.debug("Unmarshalled payload request to [" + requestObject + "]");
        }
        return requestObject;
    }

    private void marshalResponse(Object responseObject, WebServiceMessage response) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("Marshalling [" + responseObject + "] to response payload");
        }
        MarshallingUtils.marshal(getMarshaller(), responseObject, response);
    }

    protected boolean supportsInternal(MethodEndpoint methodEndpoint) {
        Method method = methodEndpoint.getMethod();
        return supportsReturnType(method) && supportsParameters(method);
    }

    private boolean supportsReturnType(Method method) {
        if (Void.TYPE.equals(method.getReturnType())) {
            return true;
        } else {
            if (getMarshaller() instanceof GenericMarshaller) {
                return ((GenericMarshaller) getMarshaller()).supports(method.getGenericReturnType());
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
