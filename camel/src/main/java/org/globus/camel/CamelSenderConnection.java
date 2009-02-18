package org.globus.camel;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultProducerTemplate;
import org.springframework.ws.WebServiceMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class CamelSenderConnection<E extends Exchange> extends AbstractCamelConnection<E> {
    private ProducerTemplate<E> template;

    public CamelSenderConnection(E exchange, ProducerTemplate<E> template, String encoding) {
        super(encoding, exchange);
        this.template = template;
    }

    public URI getUri() throws URISyntaxException {
        if(template instanceof DefaultProducerTemplate){
            return new URI(((DefaultProducerTemplate) template).getDefaultEndpoint().getEndpointUri());
        }
        return null;
    }

    @Override
    protected void onSendAfterWrite(WebServiceMessage message) throws IOException {
        super.onSendBeforeWrite(message);        
        template.send(exchange);
    }
}
