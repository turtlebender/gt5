package org.globus.wsrf;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.soap.addressing.server.annotation.Action;

@Endpoint
public class MyEndpoint {
    
    @Action("http://samples/RequestOrder")
    public void doSomething() {

    }
}
