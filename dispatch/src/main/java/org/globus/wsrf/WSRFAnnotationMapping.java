package org.globus.wsrf;

import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.EndpointInvocationChain;
import org.springframework.ws.server.EndpointMapping;
import org.springframework.ws.soap.addressing.server.SimpleActionEndpointMapping;

public class WSRFAnnotationMapping implements EndpointMapping {
    private EndpointMapping mapping;
    private SimpleActionEndpointMapping wsrfMapping;

    public EndpointMapping getMapping() {
        return mapping;
    }

    public void setMapping(EndpointMapping mapping) {
        this.mapping = mapping;
    }

    public SimpleActionEndpointMapping getWsrfMapping() {
        return wsrfMapping;
    }

    public void setWsrfMapping(SimpleActionEndpointMapping wsrfMapping) {
        this.wsrfMapping = wsrfMapping;
    }

    public EndpointInvocationChain getEndpoint(MessageContext messageContext) throws Exception {
        EndpointInvocationChain endpoint = wsrfMapping.getEndpoint(messageContext);
        if (endpoint != null)
            return endpoint;
        if(mapping == null){
            throw new Exception();
        }
        return mapping.getEndpoint(messageContext);
    }
}
