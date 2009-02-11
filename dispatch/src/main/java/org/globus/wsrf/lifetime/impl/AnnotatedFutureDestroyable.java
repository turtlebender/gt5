package org.globus.wsrf.lifetime.impl;

import org.globus.wsrf.Resourceful;
import org.globus.wsrf.annotations.AddressingAction;
import org.globus.wsrf.annotations.TerminateResource;
import org.globus.wsrf.lifetime.FutureDestroyable;
import org.oasis.wsrf.lifetime.SetTerminationTime;
import org.oasis.wsrf.lifetime.SetTerminationTimeResponse;

import javax.xml.datatype.XMLGregorianCalendar;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AnnotatedFutureDestroyable implements FutureDestroyable {
    private Object resource;
    private Method destroyMethod;
    private ScheduledExecutorService schedule;
    private Map<Object, Future<Object>> futureMap = new HashMap<Object, Future<Object>>();

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

    @AddressingAction("http://docs.oasis-open.org/wsrf/rlw-2/ScheduledResourceTermination/SetTerminationTimeRequest")
    public SetTerminationTimeResponse setTerminationTime(final @Resourceful Object id, SetTerminationTime termTime) {
        long executeTime = 0;
        if (termTime.getRequestedLifetimeDuration() != null) {
            executeTime = termTime.getRequestedLifetimeDuration().getTimeInMillis(Calendar.getInstance());
        } else if (termTime.getRequestedTerminationTime() != null) {
            XMLGregorianCalendar xmlCal = termTime.getRequestedTerminationTime().getValue();
            Calendar cal = xmlCal.toGregorianCalendar();
            executeTime = cal.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
        } else {
            Future<Object> future = futureMap.get(id);
            if (future == null) {
                return new SetTerminationTimeResponse();
            }
            future.cancel(true);
            futureMap.remove(id);
        }
        Callable<Object> terminate = new TerminateCallable(resource, id);
        Future<Object> futureResult = schedule.schedule(terminate, executeTime, TimeUnit.MILLISECONDS);
        futureMap.put(id, futureResult);
        return new SetTerminationTimeResponse();
    }

    class TerminateCallable implements Callable<Object> {
        private Object resource;
        private Object id;

        TerminateCallable(Object resource, Object id) {
            this.resource = resource;
            this.id = id;
        }

        public Object call() throws Exception {
            destroyMethod.invoke(resource, id);
            futureMap.remove(id);
            return null;
        }
    }
}
