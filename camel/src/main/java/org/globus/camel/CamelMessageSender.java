package org.globus.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.impl.DefaultProducerTemplate;
import org.springframework.ws.transport.WebServiceConnection;
import org.springframework.ws.transport.WebServiceMessageSender;

import java.io.IOException;
import java.net.URI;


public class CamelMessageSender implements WebServiceMessageSender{
    private CamelContext camelContext;

    public CamelContext getCamelContext() {
        return camelContext;
    }

    public void setCamelContext(CamelContext camelContext) {
        this.camelContext = camelContext;
    }

    public WebServiceConnection createConnection(URI uri) throws IOException {
        Exchange exchange = new DefaultExchange(camelContext);        
        DefaultProducerTemplate<Exchange> template = new DefaultProducerTemplate<Exchange>(camelContext);
        template.setDefaultEndpointUri(uri.toURL().toExternalForm());
        return  new CamelSenderConnection<Exchange>(exchange, template, "UTF-8");
    }

    public boolean supports(URI uri) {
        return uri.getScheme().equals("camel");
    }
}
