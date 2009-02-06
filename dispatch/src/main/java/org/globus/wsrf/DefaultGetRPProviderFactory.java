package org.globus.wsrf;

import org.globus.wsrf.annotations.GetResourceProperty;
import org.globus.wsrf.properties.impl.AnnotatedResourcePropertySet;
import org.globus.wsrf.properties.impl.DefaultGetResourcePropertyProvider;
import org.globus.wsrf.properties.GetResourcePropertyProvider;
import org.globus.wsrf.properties.ResourceDelegateFactory;

import java.lang.reflect.Method;

public class DefaultGetRPProviderFactory implements ResourceDelegateFactory<GetResourcePropertyProvider> {

    public boolean supports(Object o) {
        for (Method method : o.getClass().getMethods()) {
            if (method.getAnnotation(GetResourceProperty.class) != null) {
                return true;
            }
        }
        return false;
    }

    public GetResourcePropertyProvider getDelegate(Object o) {
        DefaultGetResourcePropertyProvider provider = new DefaultGetResourcePropertyProvider();
        AnnotatedResourcePropertySet propSet = new AnnotatedResourcePropertySet();
        propSet.setResource(o);
        try {
            propSet.afterPropertiesSet();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        provider.setPropSet(propSet);
        return provider;
    }

    public Class<GetResourcePropertyProvider> getInterface() {
        return GetResourcePropertyProvider.class;
    }
}
