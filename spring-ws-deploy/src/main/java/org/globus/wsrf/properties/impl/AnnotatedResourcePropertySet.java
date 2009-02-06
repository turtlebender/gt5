package org.globus.wsrf.properties.impl;

import org.globus.wsrf.annotations.GetResourceProperty;
import org.globus.wsrf.annotations.StatefulResource;
import org.globus.wsrf.properties.ResourcePropertySet;
import org.springframework.beans.factory.InitializingBean;

import javax.xml.namespace.QName;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class AnnotatedResourcePropertySet<T,V> implements ResourcePropertySet<T,V>, InitializingBean{
    private Map<QName, Method> methodMap;
    private Object resource;

    public Object getResourceClass() {
        return resource;
    }

    public void setResource(Object resource) {
        this.resource = resource;
    }


    public void afterPropertiesSet() throws Exception {
        if(resource == null){
            throw new IllegalArgumentException("You must specify the ResourceClass");
        }
        methodMap = new HashMap<QName, Method>();
        for(Method method: resource.getClass().getMethods()){
            GetResourceProperty prop = method.getAnnotation(GetResourceProperty.class);
            if(prop != null){
                method.setAccessible(true);
                methodMap.put(new QName(prop.namespace(), prop.localPart()), method);
            }
        }
        StatefulResource sr = resource.getClass().getAnnotation(StatefulResource.class);
        if(sr == null){
            throw new IllegalArgumentException("Your resource must provide a StatefulResource annotation");
        }
    }

    @SuppressWarnings("unchecked")
    public V getResourceProperty(T resourceKey, QName propName) {
        Method method = methodMap.get(propName);
        if(method == null){
            throw new IllegalArgumentException("Unknown Property Name: " + propName);
        }
        if(resource == null){
            throw new IllegalArgumentException("Resource must be specified");
        }
        try {
            return (V) method.invoke(resource, resourceKey);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to retrieve property: " + propName, e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Unable to retrieve property: " + propName, e);
        }
    }
}
