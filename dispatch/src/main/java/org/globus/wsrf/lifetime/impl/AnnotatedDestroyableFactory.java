package org.globus.wsrf.lifetime.impl;

import org.globus.wsrf.annotations.TerminateResource;
import org.globus.wsrf.lifetime.Destroyable;
import org.globus.wsrf.ResourceDelegateFactory;

import java.lang.reflect.Method;


public class AnnotatedDestroyableFactory implements ResourceDelegateFactory<Destroyable> {
    public boolean supports(Object o) {
        for (Method method : o.getClass().getMethods()) {
            TerminateResource tr = method.getAnnotation(TerminateResource.class);
            if (tr != null && tr.immediate()) {
                return true;
            }
        }
        return false;
    }

    public Destroyable getDelegate(Object o) {
        AnnotatedDestroyable destroyable = new AnnotatedDestroyable(o);
        destroyable.init();
        return destroyable;
    }

    public Class<Destroyable> getInterface() {
        return Destroyable.class;
    }
}
