package org.globus.dispatch.stateful;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.XmlType;
import javax.xml.transform.Source;


public class JAXBContextFinder {

    public JAXBContext findContext(Class<?> type) {
        XmlType typeAnnotation = type.getAnnotation(XmlType.class);
        if (typeAnnotation == null || !typeAnnotation.factoryClass().equals(XmlType.DEFAULT.class))
            return null;
        StringBuilder b = new StringBuilder(type.getPackage().getName());
        b.append(".ObjectFactory");
        Class<?> factoryClass;
        try {
            factoryClass = Thread.currentThread().getContextClassLoader().loadClass(b.toString());
        }
        catch (ClassNotFoundException e) {
            return null;
        }
        if (factoryClass.isAnnotationPresent(XmlRegistry.class)) {
            try {
                return JAXBContext.newInstance(type);
            } catch (JAXBException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Object unmarshall(Source source, Class<?> parameter) throws StateException{
        JAXBContext context = findContext(parameter);
        if(context == null){
            return null;
        }
        try {
            return context.createUnmarshaller().unmarshal(source);
        } catch (JAXBException e) {
            throw new StateException("Error unmarshalling id", e);
        }
    }
}
