package org.globus.dispatch;

import org.globus.dispatch.binding.ActionBinder;
import org.globus.dispatch.providers.ServiceProvider;
import org.globus.dispatch.utils.Converter;
import static org.globus.dispatch.utils.Converter.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.annotation.Resource;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.Detail;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.soap.Addressing;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@Addressing(enabled = true, required = false)
@WebServiceProvider
@ServiceMode(value = Service.Mode.PAYLOAD)
public class SOAPDispatchProvider implements Provider<DOMSource> {
    private Logger log = LoggerFactory.getLogger(getClass());
    TransformerFactory transFac = TransformerFactory.newInstance();
    Converter converter = new Converter();
    ActionBinder binder;
    SOAPFactory soapfac;


    public ActionBinder getBinder() {
        return binder;
    }

    public void setBinder(ActionBinder binder) {
        this.binder = binder;
    }

    @Resource
    WebServiceContext wsContext;

    @SuppressWarnings("unchecked")
    public DOMSource invoke(DOMSource source) {

        log.debug("Start invoke");
        try {
            MessageContext context = wsContext.getMessageContext();
//            String name = context.get(OperationHandler.ACTION_KEY).toString();
            ServiceProvider provider = binder.getProvider(context);
            if (provider == null) {
                DispatchException ex = new DispatchException(String.format("No provider supplied for operation:"));
                return new DOMSource(createFault(ex));
            } else {
                Class arg = getParameterType(provider);

                return convertToDOMSource(source, provider, arg);
            }
        } catch (Exception e) {
            log.warn("Unable to dispatch request", e);
            try {
                return new DOMSource(createFault(e));
            } catch (SOAPException ex) {
                log.warn("Error producing fault", ex);
                throw new RuntimeException(ex);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private DOMSource convertToDOMSource(DOMSource source, ServiceProvider provider, Class arg) throws ParserConfigurationException, IOException, SAXException, TransformerException, DispatchException, SOAPException {
        if (arg.equals(DOMSource.class)) {
            return toDOMSource(provider.invoke(source));
        } else if (arg.equals(SAXSource.class)) {
            return toDOMSource(provider.invoke(toSAXSource(source)));
        } else if (arg.equals(StreamSource.class)) {
            return toDOMSource(provider.invoke(toStreamSource(source)));
        } else {
            DispatchException ex = new DispatchException(String.format("Provider provides an unknown Source provider: %s",
                    arg.toString()));
            SOAPFault fault = createFault(ex);
            return new DOMSource(fault);
        }
    }

    private Class getParameterType(ServiceProvider provider) {
        Class<?> clazz = provider.getClass();
        Type[] types = clazz.getGenericInterfaces();
        while (types.length == 0) {
            clazz = (Class) clazz.getGenericSuperclass();
            types = clazz.getGenericInterfaces();
        }
        Type type = types[0];
        ParameterizedType pt = (ParameterizedType) type;
        Type[] args = pt.getActualTypeArguments();
        return (Class) args[0];
    }

    private SOAPFault createFault(Exception e) throws SOAPException {
        SOAPFault fault = getSOAPFactory().createFault();
        fault.setFaultString(e.getMessage());
        Detail detail = fault.addDetail();
        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        detail.setValue(writer.toString());
        return fault;
    }

    private SOAPFactory getSOAPFactory() throws SOAPException {
        if (soapfac == null) {
            soapfac = SOAPFactory.newInstance();
        }
        return soapfac;

    }

}


