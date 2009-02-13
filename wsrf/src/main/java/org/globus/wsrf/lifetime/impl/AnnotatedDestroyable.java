package org.globus.wsrf.lifetime.impl;

import org.globus.wsrf.lifetime.Destroyable;
import org.globus.wsrf.annotations.TerminateResource;
import org.globus.wsrf.annotations.AddressingAction;
import org.globus.wsrf.Resourceful;
import org.oasis.wsrf.lifetime.DestroyResponse;

import java.lang.reflect.Method;

public class AnnotatedDestroyable implements Destroyable {
    private Object resource;
    private Method destroyMethod;


    public AnnotatedDestroyable() {
    }

    public AnnotatedDestroyable(Object resource) {
        this.resource = resource;
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

    @AddressingAction("http://docs.oasis-open.org/wsrf/rlw-2/ImmediateResourceTermination/DestroyRequest")
    public DestroyResponse destroy(@Resourceful Object id) throws Exception {
        if (destroyMethod == null) {
            throw new IllegalArgumentException("Unable to dispatch terminate method");
        }
        destroyMethod.invoke(resource, id);
        return new DestroyResponse();
    }
}
