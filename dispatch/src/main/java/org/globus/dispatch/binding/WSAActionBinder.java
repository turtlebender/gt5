package org.globus.dispatch.binding;

import org.globus.dispatch.binding.OperationHandler;

import javax.xml.ws.handler.MessageContext;


public class WSAActionBinder extends AbstractActionBinder {

    public Object getActionKey(MessageContext context) {
        return context.get(OperationHandler.ACTION_KEY);
    }
}
