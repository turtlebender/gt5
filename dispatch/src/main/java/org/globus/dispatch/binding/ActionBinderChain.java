package org.globus.dispatch.binding;

import org.globus.dispatch.providers.ServiceProvider;

import javax.xml.ws.handler.MessageContext;
import java.util.ArrayList;
import java.util.List;

/**
 * This will iterate through a collection of ActionBinder implementations and return the first ServiceProvider that it
 * finds.
 *
 * @author Tom Howe
 */
public class ActionBinderChain implements ActionBinder{
    List<ActionBinder> binderChain;

    public List<ActionBinder> getBinderChain() {
        if(binderChain == null){
            binderChain = new ArrayList<ActionBinder>();
        }
        return binderChain;
    }

    public void setBinderChain(List<ActionBinder> binderChain) {
        this.binderChain = binderChain;
    }

    public ServiceProvider getProvider(MessageContext context) {
        for(ActionBinder binder: binderChain){
            ServiceProvider provider = binder.getProvider(context);
            if(provider != null){
                return provider;
            }
        }
        return null; 
    }
}
