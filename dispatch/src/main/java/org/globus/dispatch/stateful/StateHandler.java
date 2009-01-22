package org.globus.dispatch.stateful;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.ws.WebServiceContext;
import javax.annotation.Resource;
import java.util.Iterator;
import java.util.Set;


public class StateHandler implements SOAPHandler<SOAPMessageContext> {
    Logger logger = LoggerFactory.getLogger(getClass());
    public static final String REF_PARAMS = "globus.state.referenceParams";

    @Resource
    private WebServiceContext context;

    public Set<QName> getHeaders() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean handleMessage(SOAPMessageContext soapMessageContext) {
        if (!((Boolean) soapMessageContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY))) {
            SOAPMessage message = soapMessageContext.getMessage();
            SOAPPart part = message.getSOAPPart();                              
            try {
                SOAPEnvelope env = part.getEnvelope();
                SOAPHeader header = env.getHeader();
                Iterator it = header.getChildElements(new QName("http://www.w3.org/2005/08/addressing", "ReferenceProperties"));
                logger.debug("getting reference properties");
                while (it.hasNext()) {
                    SOAPElement referenceParams = (SOAPElement) it.next();                    
                    context.getMessageContext().put(REF_PARAMS, referenceParams);
                    context.getMessageContext().setScope(REF_PARAMS, MessageContext.Scope.APPLICATION);                                        
                }
            } catch (Exception e) {
                logger.warn("Unable to acquire addressing reference properties.", e);
                return false;
            }
        }
        return true;
    }

    public boolean handleFault(SOAPMessageContext soapMessageContext) {
        return true;
    }

    public void close(MessageContext messageContext) {
       
    }
}
