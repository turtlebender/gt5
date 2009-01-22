package org.globus.dispatch.stateful;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.globus.dispatch.utils.Converter.toDOMSource;
import static org.globus.dispatch.utils.Converter.toSAXSource;
import static org.globus.dispatch.utils.Converter.toStreamSource;
import org.xml.sax.SAXException;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.dom.DOMSource;
import javax.xml.parsers.ParserConfigurationException;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: turtlebender
 * Date: Jan 22, 2009
 * Time: 12:56:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class DefaultDeleter {

    Logger logger = LoggerFactory.getLogger(getClass());
    JAXBContextFinder finder;
    Method method;
    Object target;

    public Object invoke(Source o) throws StateException{
        Object key;
        Class[] clazzes = method.getParameterTypes();
        if (clazzes.length > 1) {
            throw new StateException(String.format("Stateful resource methods should only have a single parameter." +
                    "  You supplied %d for method %s", clazzes.length, method.getName()));
        }
        Class paramType = clazzes[0];
        if (Source.class.isAssignableFrom(paramType)) {
            key = processSource(o);
        } else {
            key = finder.unmarshall(o, method.getClass());
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

    Object processSource(Source source) throws StateException {
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
