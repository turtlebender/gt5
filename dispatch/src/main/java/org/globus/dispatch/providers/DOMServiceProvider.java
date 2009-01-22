package org.globus.dispatch.providers;

import org.globus.dispatch.exception.DispatchException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;

public abstract class DOMServiceProvider implements ServiceProvider<DOMSource> {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    public DOMSource invoke(DOMSource source) throws DispatchException {
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Element result = this.invokeInternal(getElement(source), builder.newDocument());
            return new DOMSource(result);
        } catch (ParserConfigurationException e) {
            throw new DispatchException(e);
        } catch (Exception e) {
            throw new DispatchException(e);
        }
    }

    private Element getElement(DOMSource source) {
        Node node = source.getNode();
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            return (Element) node;
        } else if (node.getNodeType() == Node.DOCUMENT_NODE) {
            return ((Document) node).getDocumentElement();
        }else{
            throw new IllegalArgumentException("Root node of Source is neither an Element nor a Document");
        }
    }

    public abstract Element invokeInternal(Element element, Document response) throws Exception;
}
