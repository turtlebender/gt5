package org.globus.wsrf.properties.impl;

import org.globus.wsrf.annotations.GetResourceProperty;
import org.globus.wsrf.ResourceDelegateFactory;
import org.globus.wsrf.properties.GetResourcePropertyProvider;

import java.lang.reflect.Method;

public class AnnotatedGetRPFactory implements ResourceDelegateFactory<GetResourcePropertyProvider> {

    public boolean supports(Object o) {
        for (Method method : o.getClass().getMethods()) {
            if (method.getAnnotation(GetResourceProperty.class) != null) {
                return true;
            }
        }
        return false;
    }

    public GetResourcePropertyProvider getDelegate(Object o) {
        AnnotatedGetResourcePropertyProvider provider = new AnnotatedGetResourcePropertyProvider(o);
        try {
            provider.afterPropertiesSet();
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to configure ResourceProperties");
        }
        return provider;
    }

    public Class<GetResourcePropertyProvider> getInterface() {
        return GetResourcePropertyProvider.class;
    }
}
