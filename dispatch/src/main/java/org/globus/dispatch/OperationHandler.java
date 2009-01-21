package org.globus.dispatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.util.Iterator;
import java.util.Set;

public class OperationHandler implements SOAPHandler<SOAPMessageContext> {
    Logger logger = LoggerFactory.getLogger(getClass());
    public static final String ACTION_KEY = "globus.action.key";
    
    @Resource
    private WebServiceContext context;

    public Set<QName> getHeaders() {
        return null;
    }

    public boolean handleMessage(SOAPMessageContext soapMessageContext) {
        if (!((Boolean) soapMessageContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY))) {
            SOAPMessage message = soapMessageContext.getMessage();
            SOAPPart part = message.getSOAPPart();
            try {
                SOAPEnvelope env = part.getEnvelope();
                SOAPHeader header = env.getHeader();
                Iterator it = header.getChildElements(new QName("http://www.w3.org/2005/08/addressing", "Action"));
                logger.info("getting operation action");
                while (it.hasNext()) {
                    SOAPElement action = (SOAPElement) it.next();
                    String actionString = action.getValue();
                    soapMessageContext.put(ACTION_KEY, actionString);
                    soapMessageContext.setScope(ACTION_KEY, MessageContext.Scope.APPLICATION);
                    System.out.println("actionName = " + actionString);
                }
            } catch (Exception e) {
                logger.warn("Unable to acquire addressing action.", e);
                return false;
            }
        }
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean handleFault(SOAPMessageContext soapMessageContext) {
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void close(MessageContext messageContext) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
