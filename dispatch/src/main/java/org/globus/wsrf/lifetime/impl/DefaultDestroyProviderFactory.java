package org.globus.wsrf.lifetime.impl;

import org.globus.wsrf.annotations.TerminateResource;
import org.globus.wsrf.lifetime.DestroyProvider;
import org.globus.wsrf.properties.ResourceDelegateFactory;

import java.lang.reflect.Method;


public class DefaultDestroyProviderFactory implements ResourceDelegateFactory<DestroyProvider>{
    public boolean supports(Object o) {
        for (Method method : o.getClass().getMethods()) {
            TerminateResource tr = method.getAnnotation(TerminateResource.class);
            if (tr != null && tr.immediate()) {
                return true;
            }
        }
        return false;
    }

    public DestroyProvider getDelegate(Object o) {
        AnnotatedDestroyable destroyable = new AnnotatedDestroyable(o);
        destroyable.init();
        return new DefaultDestroyProvider(destroyable);        
    }

    public Class<DestroyProvider> getInterface() {
        return DestroyProvider.class;
    }
}
