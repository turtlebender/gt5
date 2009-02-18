package org.globus.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.ws.transport.support.SimpleWebServiceMessageReceiverObjectSupport;

import java.net.URI;


public class CamelMessageReceiver extends SimpleWebServiceMessageReceiverObjectSupport implements Processor {
    public static final String DEFAULT_TEXT_MESSAGE_ENCODING = "UTF-8";
    private String textMessageEncoding = DEFAULT_TEXT_MESSAGE_ENCODING;
    private URI uri;

    public CamelMessageReceiver(URI uri) {        
        this.uri = uri;        
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public void setTextMessageEncoding(String textMessageEncoding) {
        this.textMessageEncoding = textMessageEncoding;
    }

    public void process(Exchange exchange) throws Exception {
        CamelReceiverConnection<Exchange> connection =
                new CamelReceiverConnection<Exchange>(textMessageEncoding, exchange, uri);
        handleConnection(connection);
    }
}
