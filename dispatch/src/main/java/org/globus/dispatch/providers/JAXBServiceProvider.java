package org.globus.dispatch.providers;

import org.globus.dispatch.exception.DispatchException;
import org.globus.dispatch.binding.DispatchBinding;
import org.xml.sax.InputSource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.sax.SAXSource;

public class JAXBServiceProvider implements ServiceProvider<SAXSource> {
    private JAXBContext context;
    private DispatchBinding binding;

    public JAXBContext getContext() {
        return context;
    }

    public void setContext(JAXBContext context) {
        this.context = context;
    }

    public DispatchBinding getBinding() {
        return binding;
    }

    public void setBinding(DispatchBinding binding) {
        this.binding = binding;
    }

    public SAXSource invoke(SAXSource source) throws DispatchException {
        try {
            Unmarshaller unmarshaller = context.createUnmarshaller();
            Object o = unmarshaller.unmarshal(source);
            Object result = binding.dispatch(o);
            java.io.StringWriter sw = new java.io.StringWriter();
            this.context.createMarshaller().marshal(result, sw);
            InputSource isource = new InputSource(new java.io.StringReader(sw.toString()));
            return new SAXSource(isource);
        } catch (JAXBException e) {
            throw new DispatchException(e);
        }        
    }
}
