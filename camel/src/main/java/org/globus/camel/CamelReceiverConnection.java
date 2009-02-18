package org.globus.camel;

import org.apache.camel.Exchange;

import java.net.URI;
import java.net.URISyntaxException;


public class CamelReceiverConnection<E extends Exchange> extends AbstractCamelConnection<E>{
    private URI uri;

    public CamelReceiverConnection(String encoding, E exchange, URI uri) {
        super(encoding, exchange);
        this.uri = uri;        
    }

    public URI getUri() throws URISyntaxException {
        return uri;
    }
}
