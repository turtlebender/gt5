package org.globus.wsrf.lifetime.impl;

import org.globus.wsrf.annotations.TerminateResource;
import org.globus.wsrf.lifetime.FutureDestroyable;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AnnotatedFutureDestroyable implements FutureDestroyable{
    private Object resource;
    private Method destroyMethod;
    private ScheduledExecutorService schedule;

    public AnnotatedFutureDestroyable() {
    }

    public AnnotatedFutureDestroyable(Object resource) {
        this.resource = resource;
    }

    public ScheduledExecutorService getSchedule() {
        return schedule;
    }

    public void setSchedule(ScheduledExecutorService schedule) {
        this.schedule = schedule;
    }

    public Object getResource() {
        return resource;
    }

    public void setResource(Object resource) {
        this.resource = resource;
    }

    public void init() {
        if (resource == null) {
            throw new IllegalArgumentException("You must specify the ResourceClass");
        }
        for (Method method : resource.getClass().getMethods()) {
            if (method.getAnnotation(TerminateResource.class) != null) {
                destroyMethod = method;
                break;
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void setTerminationTime(Calendar cal) {
        Callable terminate = new Callable(){
            public Object call() throws Exception{
                destroyMethod.invoke(resource);
                return null;
            }
        };
        long executeTime = cal.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
        schedule.schedule(terminate, executeTime, TimeUnit.MILLISECONDS);
    }
}
