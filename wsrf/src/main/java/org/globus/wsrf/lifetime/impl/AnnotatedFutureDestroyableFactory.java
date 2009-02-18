package org.globus.wsrf.lifetime.impl;

import org.globus.wsrf.annotations.TerminateResource;
import org.globus.wsrf.lifetime.FutureDestroyable;
import org.globus.wsrf.ResourceDelegateFactory;

import java.lang.reflect.Method;
import java.util.concurrent.ScheduledExecutorService;


public class AnnotatedFutureDestroyableFactory implements ResourceDelegateFactory<FutureDestroyable> {
    private ScheduledExecutorService schedule;

    public AnnotatedFutureDestroyableFactory() {
    }

    public AnnotatedFutureDestroyableFactory(ScheduledExecutorService schedule) {
        this.schedule = schedule;
    }

    public ScheduledExecutorService getSchedule() {
        return schedule;
    }

    public void setSchedule(ScheduledExecutorService schedule) {
        this.schedule = schedule;
    }

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
        AnnotatedFutureDestroyable destroy = new AnnotatedFutureDestroyable(o, schedule);
        destroy.init();
        return destroy;
    }

    public Class<FutureDestroyable> getInterface() {
        return FutureDestroyable.class;
    }
}
