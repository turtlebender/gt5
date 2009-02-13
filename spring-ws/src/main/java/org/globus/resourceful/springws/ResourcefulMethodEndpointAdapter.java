package org.globus.resourceful.springws;

import org.globus.wsrf.WebMethodInvoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.MethodEndpoint;
import org.springframework.ws.server.endpoint.adapter.GenericMarshallingMethodEndpointAdapter;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.support.MarshallingUtils;

import javax.xml.transform.Source;
import java.io.IOException;
import java.lang.reflect.Method;


public class ResourcefulMethodEndpointAdapter extends GenericMarshallingMethodEndpointAdapter {
    private WebMethodInvoker invoker = new WebMethodInvoker();
    private Logger logger = LoggerFactory.getLogger(getClass());

    public WebMethodInvoker getInvoker() {
        return invoker;
    }

    public void setInvoker(WebMethodInvoker invoker) {
        this.invoker = invoker;
    }

    protected void invokeInternal(MessageContext messageContext, MethodEndpoint methodEndpoint) throws Exception {
        try {
            SoapMessage request = (SoapMessage) messageContext.getRequest();
            Object requestObject = unmarshalRequest(request);
            Source headerSource = request.getSoapHeader().getSource();
            Method method = methodEndpoint.getMethod();
            Object target = methodEndpoint.getBean();
            Object responseObject = invoker.invoke(method, target, requestObject, headerSource,
                    new SpringWSPropertyHolder(messageContext));
            if (responseObject != null) {
                WebServiceMessage response = messageContext.getResponse();
                marshalResponse(responseObject, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private Object unmarshalRequest(WebServiceMessage request) throws IOException {
        Object requestObject = MarshallingUtils.unmarshal(getUnmarshaller(), request);
        logger.debug("Unmarshalled payload request to [ {} ]", requestObject);
        return requestObject;
    }

    private void marshalResponse(Object responseObject, WebServiceMessage response) throws IOException {
        logger.debug("Marshalling [ {} ] to response payload", responseObject);
        MarshallingUtils.marshal(getMarshaller(), responseObject, response);
    }

    protected boolean supportsInternal(MethodEndpoint methodEndpoint) {
        return invoker.supports(methodEndpoint.getMethod());
    }
}
