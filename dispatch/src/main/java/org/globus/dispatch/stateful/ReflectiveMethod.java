package org.globus.dispatch.stateful;

import static org.globus.dispatch.utils.Converter.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectiveMethod {

    Logger logger = LoggerFactory.getLogger(getClass());
    JAXBContextFinder finder;
    Method method;
    Object target;

    public ReflectiveMethod() {
    }

    public ReflectiveMethod(JAXBContextFinder finder, Method method, Object target) {
        setFinder(finder);
        setMethod(method);
        setTarget(target);
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        method.setAccessible(true);
        this.method = method;
    }

    public JAXBContextFinder getFinder() {
        return finder;
    }

    public void setFinder(JAXBContextFinder finder) {
        this.finder = finder;
    }

    public Object invoke(Object o) throws StateException {
        Object key;
        Class[] clazzes = method.getParameterTypes();
        if (clazzes.length > 1) {
            throw new StateException(String.format("Stateful resource methods should only have a single parameter." +
                    "  You supplied %d for method %s", clazzes.length, method.getName()));
        }
        Class paramType = clazzes[0];
        if (String.class.isAssignableFrom(paramType)) {
            key = processString(o, paramType);
        } else if (Source.class.isAssignableFrom(paramType)) {
            if(!(o instanceof Source)){
                throw new IllegalArgumentException(String.format("Parameter type: %s is not the same as method signature " +
                        "defines: %s", o.getClass().getName(), paramType.getName()));
            }
            key = processSource((Source) o);
        } else {
            key = finder.unmarshall((Source) o, method.getClass());
        }
        try {
            return method.invoke(target, key);
        } catch (IllegalAccessException e) {
            logger.warn("Unable to invoke finder", e);
            throw new StateException(String.format("Unable to invoke finder: %s", method.getName()), e);
        } catch (InvocationTargetException e) {
            logger.warn("Unable to invoke finder", e);
            throw new StateException(String.format("Unable to invoke finder: %s", method.getName()), e);
        }
    }

    private Object processString(Object o, Class paramType) {
        Object key;
        if(!(o instanceof String)){
            throw new IllegalArgumentException(String.format("Parameter type: %s is not the same as method signature " +
                    "defines: %s", o.getClass().getName(), paramType.getName()));
        }
        key = o;
        return key;
    }

    private Object processSource(Source source) throws StateException {

        try {

            if (source.getClass().equals(DOMSource.class)) {
                DOMSource dom = toDOMSource(source);
                return method.invoke(target, dom);
            } else if (source.getClass().equals(SAXSource.class)) {
                SAXSource sax = toSAXSource(source);
                return method.invoke(target, sax);
            } else if (source.getClass().equals(StreamSource.class)) {
                StreamSource stream = toStreamSource(source);
                return method.invoke(target, stream);
            } else {
                logger.warn("Unknown stream type: {}", source.getClass().getName());
                throw new IllegalArgumentException(String.format("Unknown Stream Type: %s", source.getClass()));
            }
        } catch (IllegalAccessException e) {
            logger.warn("Unable to invoke finder", e);
            throw new StateException(String.format("Unable to invoke finder: %s", method.getName()), e);
        } catch (InvocationTargetException e) {
            logger.warn("Unable to invoke finder", e);
            throw new StateException(String.format("Unable to invoke finder: %s", method.getName()), e);
        } catch (TransformerException e) {
            logger.warn("Unable to invoke finder", e);
            throw new StateException(String.format("Unable to invoke finder: %s", method.getName()), e);
        } catch (IOException e) {
            logger.warn("Unable to invoke finder", e);
            throw new StateException(String.format("Unable to invoke finder: %s", method.getName()), e);
        } catch (ParserConfigurationException e) {
            logger.warn("Unable to invoke finder", e);
            throw new StateException(String.format("Unable to invoke finder: %s", method.getName()), e);
        } catch (SAXException e) {
            logger.warn("Unable to invoke finder", e);
            throw new StateException(String.format("Unable to invoke finder: %s", method.getName()), e);
        }
    }
}
