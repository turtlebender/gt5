package org.globus.dispatch.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.globus.common.PropertyHolder;
import org.globus.wsrf.InvokeMethodRequest;

import java.util.HashMap;
import java.util.Map;


public class ResourcefulMessageEnhancer implements Processor {
    private Map<String, CamelMethodEndpoint> endpointMap = new HashMap<String, CamelMethodEndpoint>();
    
    public void registerEndpoint(String address, CamelMethodEndpoint endpoint){
        endpointMap.put(address, endpoint);
    }

    public void process(Exchange exchange) throws Exception {
        Message inMessage = exchange.getIn();
        String address = inMessage.getHeader("target.address").toString();
        Object resourceKey = inMessage.getHeader("target.resource.key");
        CamelMethodEndpoint endpoint = endpointMap.get(address);
        InvokeMethodRequest request = new InvokeMethodRequest(endpoint.getTarget(), inMessage.getBody(),
                new CamelPropertyHolder(inMessage), resourceKey, endpoint.getTargetMethod());
        inMessage.setBody(request);
    }

    class CamelPropertyHolder implements PropertyHolder {
        Message message;

        CamelPropertyHolder(Message message) {
            this.message = message;
        }

        public void setProperty(String name, Object value) {
            message.setHeader(name, value);
        }

        public Object getProperty(String name) {
            return message.getHeader(name);
        }
    }
}
