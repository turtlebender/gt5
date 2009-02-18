package org.globus.camel;

import org.apache.camel.Exchange;
import org.springframework.ws.transport.AbstractSenderConnection;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: turtlebender
 * Date: Feb 17, 2009
 * Time: 5:08:09 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractCamelConnection<E extends Exchange> extends AbstractSenderConnection {
    protected E exchange;
    protected String encoding;
    

    public AbstractCamelConnection(String encoding, E exchange) {
        this.encoding = encoding;
        this.exchange = exchange;
    }



    protected boolean hasResponse() throws IOException {
        return true;
    }

    protected void addRequestHeader(String name, String value) throws IOException {
        exchange.getIn().setHeader(name, value);
    }

    protected OutputStream getRequestOutputStream() throws IOException {
        return new CamelMessageOutputStream(exchange.getIn(), encoding);
    }

    protected Iterator getResponseHeaderNames() throws IOException {
        return exchange.getOut().getHeaders().keySet().iterator();
    }

    protected Iterator getResponseHeaders(String name) throws IOException {
        return Collections.singleton(exchange.getOut().getHeader(name)).iterator();
    }

    protected InputStream getResponseInputStream() throws IOException {
        Object body = exchange.getIn().getBody();
        if(body != null && body instanceof String){
            return new ByteArrayInputStream(((String) body).getBytes(encoding));
        }
        return new ByteArrayInputStream(new byte[0]);
    }

    public abstract URI getUri() throws URISyntaxException;

    public boolean hasError() throws IOException {
        return exchange.isFailed();
    }

    public String getErrorMessage() throws IOException {
        return exchange.getException().getMessage();
    }
}
