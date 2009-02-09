package org.globus.wsrf.lifetime.impl;

import org.globus.wsrf.properties.ResourceDelegateFactory;
import org.globus.wsrf.annotations.TerminateResource;
import org.globus.wsrf.lifetime.SetTerminationTimeProvider;

import java.lang.reflect.Method;


public class DefaultFutureDestroyableProviderFactory implements ResourceDelegateFactory<SetTerminationTimeProvider> {
    public boolean supports(Object o) {
        for (Method method : o.getClass().getMethods()) {
            TerminateResource tr = method.getAnnotation(TerminateResource.class);
            if (tr != null && tr.scheduled()) {
                return true;
            }
        }
        return false;
    }

    public SetTerminationTimeProvider getDelegate(Object o) {
        AnnotatedFutureDestroyable destroy = new AnnotatedFutureDestroyable(o);
        destroy.init();
        return new DefaultSetTerminationTimeProvider(destroy);
    }

    public Class<SetTerminationTimeProvider> getInterface() {
        return SetTerminationTimeProvider.class;
    }
}
