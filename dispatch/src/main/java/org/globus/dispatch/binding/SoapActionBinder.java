package org.globus.dispatch.binding;

import javax.xml.ws.handler.MessageContext;
import java.util.Map;
import java.util.List;

public class SoapActionBinder extends AbstractActionBinder {
    
    public Object getActionKey(MessageContext context) {
        Map map = (Map) context.get(MessageContext.HTTP_REQUEST_HEADERS);
        String soapAction = (String) ((List) map.get("SOAPAction")).get(0);
        return soapAction.substring(1, soapAction.length() - 1);
    }
}
