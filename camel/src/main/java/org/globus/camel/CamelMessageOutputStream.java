package org.globus.camel;

import org.apache.camel.Message;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: turtlebender
 * Date: Feb 17, 2009
 * Time: 4:16:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class CamelMessageOutputStream extends FilterOutputStream {
    private Message message;
    private String encoding;

    public CamelMessageOutputStream(Message message, String encoding) {
        super(new ByteArrayOutputStream());
        this.message = message;
        this.encoding = encoding;
    }

    @Override
    public void flush() throws IOException {
        super.flush();
        ByteArrayOutputStream baos = (ByteArrayOutputStream) out;
        String text = new String(baos.toByteArray(), encoding);
        message.setBody(text);
    }
}
