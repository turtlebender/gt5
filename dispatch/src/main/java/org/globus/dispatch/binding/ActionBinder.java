package org.globus.dispatch.binding;

import org.globus.dispatch.providers.ServiceProvider;

import javax.xml.ws.handler.MessageContext;

/**
 * Created by IntelliJ IDEA.
 * User: turtlebender
 * Date: Jan 21, 2009
 * Time: 11:54:28 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ActionBinder {
    ServiceProvider getProvider(MessageContext context);
}
