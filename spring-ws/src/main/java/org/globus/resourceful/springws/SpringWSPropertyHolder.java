package org.globus.resourceful.springws;

import org.globus.common.PropertyHolder;
import org.springframework.ws.context.MessageContext;


public class SpringWSPropertyHolder implements PropertyHolder {
    private MessageContext context;

    public SpringWSPropertyHolder(MessageContext context) {
        this.context = context;
    }

    public MessageContext getContext() {
        return context;
    }

    public void setContext(MessageContext context) {
        this.context = context;
    }

    public void setProperty(String name, Object value) {
        context.setProperty(name, value);
    }

    public Object getProperty(String name) {
        return context.getProperty(name);
    }
}
