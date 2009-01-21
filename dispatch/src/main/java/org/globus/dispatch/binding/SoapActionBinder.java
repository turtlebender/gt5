package org.globus.dispatch.binding;

import org.globus.dispatch.providers.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.ws.handler.MessageContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: turtlebender
 * Date: Jan 16, 2009
 * Time: 3:58:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class SoapActionBinder {
    private Map<String, ServiceProvider> providerMap = new HashMap<String, ServiceProvider>();
    private Logger logger = LoggerFactory.getLogger(getClass());

    public void bindAction(String soapAction, ServiceProvider provider){
        providerMap.put(soapAction, provider);
    }

    public ServiceProvider getProvider(MessageContext context){
        Map map = (Map) context.get(MessageContext.HTTP_REQUEST_HEADERS);
        String soapAction = (String) ((List)map.get("SOAPAction")).get(0);
        soapAction = soapAction.substring(1, soapAction.length() - 1);
        logger.debug("SoapAction: {}", soapAction);
        return providerMap.get(soapAction);
    }

    public Map<String, ServiceProvider> getProviderMap() {
        return providerMap;
    }

    public void setProviderMap(Map<String, ServiceProvider> providerMap) {
        this.providerMap = providerMap;
    }
}
