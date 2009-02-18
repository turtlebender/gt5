package org.globus.wsrf.properties.impl;

import org.globus.wsrf.ResourceException;
import org.globus.wsrf.Resourceful;
import org.globus.wsrf.annotations.AddressingAction;
import org.globus.wsrf.annotations.GetResourceProperty;
import org.globus.wsrf.annotations.StatefulResource;
import org.globus.wsrf.properties.GetResourcePropertyProvider;
import org.globus.wsrf.properties.UnknownResourcePropertyException;
import org.oasis.wsrf.resourceproperties.GetResourcePropertyResponse;
import org.springframework.beans.factory.InitializingBean;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class AnnotatedGetResourcePropertyProvider implements GetResourcePropertyProvider, InitializingBean {
    private Map<QName, Method> methodMap = new HashMap<QName, Method>();
    private Object resource;

    public AnnotatedGetResourcePropertyProvider() {
    }

    public AnnotatedGetResourcePropertyProvider(Object resource) {
        this.resource = resource;
    }

    public Object getResource() {
        return resource;
    }

    public void setResource(Object resource) {
        this.resource = resource;
    }


    public void afterPropertiesSet() throws Exception {
        if (resource == null) {
            throw new IllegalArgumentException("You must specify the ResourceClass");
        }
        for (Method method : resource.getClass().getMethods()) {
            GetResourceProperty prop = method.getAnnotation(GetResourceProperty.class);
            if (prop != null) {
                method.setAccessible(true);
                methodMap.put(new QName(prop.namespace(), prop.localPart()), method);
            }
        }
        StatefulResource sr = resource.getClass().getAnnotation(StatefulResource.class);
        if (sr == null) {
            throw new IllegalArgumentException("Your resource must provide a StatefulResource annotation");
        }
    }

    @AddressingAction(namespace = "http://docs.open-oasis.org/wsrf/rpw-2/GetResourceProperty/",
            path = "GetResourcePropertyRequest")
    @SuppressWarnings("unchecked")
    public GetResourcePropertyResponse getResourceProperty(@Resourceful Object resourceKey, JAXBElement<QName> resourceProperty)
            throws ResourceException {
        QName propName = resourceProperty.getValue();
        if (resource == null) {
            throw new IllegalArgumentException("Resource must be specified");
        }
        Method method = methodMap.get(propName);
        if (method == null) {
            throw new UnknownResourcePropertyException("Unknown Property Name: " + propName);
        }
        try {
            Object result;
            if (method.getParameterTypes().length == 0) {
                result = method.invoke(resource);
            } else {
                result = method.invoke(resource, resourceKey);
            }
            JAXBElement element = new JAXBElement(propName, result.getClass(), result);
            GetResourcePropertyResponse response = new GetResourcePropertyResponse();
            response.getAny().add(element);
            return response;
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to retrieve property: " + propName, e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Unable to retrieve property: " + propName, e);
        }
    }
}
