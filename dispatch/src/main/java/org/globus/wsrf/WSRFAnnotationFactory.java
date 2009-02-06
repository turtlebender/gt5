package org.globus.wsrf;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.ws.server.EndpointMapping;
import org.springframework.ws.soap.addressing.server.SimpleActionEndpointMapping;

import java.util.Properties;


public class WSRFAnnotationFactory implements FactoryBean {
    private EndpointMapping delegate;
    private Object resource;
    private String portName;
    private String namespace;

    public Object getResource() {
        return resource;
    }

    public void setResource(Object resource) {
        this.resource = resource;
    }

    public EndpointMapping getDelegate() {
        return delegate;
    }

    public void setDelegate(EndpointMapping delegate) {
        this.delegate = delegate;
    }

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    private String getAction(String end) {
        return namespace + "/" + portName + "/" + end;
    }


    public Object getObject() throws Exception {
        WSRFAnnotationMapping mapping = new WSRFAnnotationMapping();
        SimpleActionEndpointMapping wsrfMapping = new SimpleActionEndpointMapping();
        Properties actionMap = new Properties();
        actionMap.put(getAction("GetResourcePropertyRequest"), "getRP");
//        actionMap.put(getAction("DestroyRequest"), new DestroyProvider());
//        actionMap.put(getAction("GetMultipleResourcePropertyRequest"), new GetMultipleResourcePropertyProvider());
//        actionMap.put(getAction("GetCurrentMessageRequest"), new GetCurrentMessageProvider());
//        actionMap.put(getAction("SetTerminationTimeRequest"), new SetTerminationTimeProvider());
//        actionMap.put(getAction("SubscribeRequest"), new SubscribeProvider());
        wsrfMapping.setMappings(actionMap);
        mapping.setMapping(delegate);
        mapping.setWsrfMapping(wsrfMapping);
        return mapping;
    }

    public Class getObjectType() {
        return EndpointMapping.class;
    }

    public boolean isSingleton() {
        return true;
    }
}
