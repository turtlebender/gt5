package org.globus.dispatch.binding;

import org.globus.dispatch.providers.ServiceProvider;

import javax.xml.ws.handler.MessageContext;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractActionBinder implements ActionBinder {
    Map<Object, ServiceProvider> providerMap = new HashMap<Object, ServiceProvider>();

    public ServiceProvider getProvider(MessageContext context) {
        ServiceProvider provider = null;
        Object key = getActionKey(context);
        if (key != null) {
            return providerMap.get(key.toString());
        }
        return provider;
    }

    public void bindAction(Object key, ServiceProvider provider) {
        providerMap.put(key, provider);
    }

    public Map<Object, ServiceProvider> getProviderMap() {
        return providerMap;
    }

    public void setProviderMap(Map<Object, ServiceProvider> providerMap) {
        this.providerMap = providerMap;
    }

    public abstract Object getActionKey(MessageContext context);
}
