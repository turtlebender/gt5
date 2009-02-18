package org.globus.wsrf.common;

import org.globus.wsrf.Resourceful;
import org.globus.wsrf.annotations.TerminateResource;
import org.globus.wsrf.annotations.StatefulResource;
import org.globus.wsrf.annotations.GetResourceProperty;

@StatefulResource(keyNamespace = "http://www.sample.com", keyLocalpart = "SampleKey",
        resourceNamespace = "http://www.sample.com", resourceLocalpart = "SampleResource")
public class SampleResource {
    public static enum ResourceState{ACTIVE, INACTIVE, DESTROYED}
    public static final Object SINGLETON_STATE = new Object();

    private ResourceState state = ResourceState.ACTIVE;
    
    @GetResourceProperty(namespace = "http://www.sample.com", localPart = "SampleResource/StatefulState")
    public ResourceState getState(@Resourceful String resourceId) {
        return state;
    }

    @GetResourceProperty(namespace="http://www.sample.com", localPart = "SampleResource/SingletonState")
    public Object getSingletonState(){
        return SINGLETON_STATE;
    }

    public void setState(ResourceState state) {
        this.state = state;
    }

    @TerminateResource(immediate = true, scheduled = true)
    public void destroy(@Resourceful String id){
        state = ResourceState.DESTROYED;
    }
}
