package org.globus.wsrf.lifetime.impl;

import org.globus.wsrf.annotations.TerminateResource;
import org.globus.wsrf.lifetime.FutureDestroyable;
import org.globus.wsrf.ResourceDelegateFactory;

import java.lang.reflect.Method;


public class AnnotatedFutureDestroyableFactory implements ResourceDelegateFactory<FutureDestroyable> {
    public boolean supports(Object o) {
        for (Method method : o.getClass().getMethods()) {
            TerminateResource tr = method.getAnnotation(TerminateResource.class);
            if (tr != null && tr.scheduled()) {
                return true;
            }
        }
        return false;
    }

    public FutureDestroyable getDelegate(Object o) {
        AnnotatedFutureDestroyable destroy = new AnnotatedFutureDestroyable(o);
        destroy.init();
        return destroy;
    }

    public Class<FutureDestroyable> getInterface() {
        return FutureDestroyable.class;
    }
}
