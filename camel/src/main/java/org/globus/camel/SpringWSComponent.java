package org.globus.camel;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.apache.camel.impl.ProcessorEndpoint;
import org.springframework.ws.WebServiceMessageFactory;
import org.springframework.ws.transport.WebServiceMessageReceiver;

import java.net.URI;
import java.util.Map;

public class SpringWSComponent extends DefaultComponent{
    private WebServiceMessageReceiver messageReceiver;
    private WebServiceMessageFactory messageFactory;

    public WebServiceMessageReceiver getMessageReceiver() {
        return messageReceiver;
    }

    public void setMessageReceiver(WebServiceMessageReceiver messageReceiver) {
        this.messageReceiver = messageReceiver;
    }

    public WebServiceMessageFactory getMessageFactory() {
        return messageFactory;
    }

    public void setMessageFactory(WebServiceMessageFactory messageFactory) {
        this.messageFactory = messageFactory;
    }

    protected Endpoint createEndpoint(String uri, String remaining, Map parameters) throws Exception {
        CamelMessageReceiver receiver = new CamelMessageReceiver(new URI(uri));
        return new ProcessorEndpoint(uri, receiver);
    }
}
