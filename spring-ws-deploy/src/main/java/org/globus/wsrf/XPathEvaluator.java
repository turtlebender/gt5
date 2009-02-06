package org.globus.wsrf;

import org.springframework.xml.transform.TraxUtils;
import org.springframework.xml.transform.TransformerObjectSupport;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.util.Iterator;

public class XPathEvaluator extends TransformerObjectSupport{
    private XPathFactory fac = XPathFactory.newInstance();
    private XPathExpression xpath;

    public void setQName(final QName name) throws XPathException {
        XPath xp = fac.newXPath();
        xp.setNamespaceContext(new NamespaceContext() {
            public String getNamespaceURI(String prefix) {
                if (prefix.equals("user")) {
                    return name.getNamespaceURI();
                } else {
                    return XMLConstants.NULL_NS_URI;
                }
            }

            public String getPrefix(String namespaceURI) {
                throw new UnsupportedOperationException();
            }

            public Iterator getPrefixes(String namespaceURI) {
                throw new UnsupportedOperationException();
            }
        });
        String expression = String.format("//user:%s", name.getLocalPart());
        xpath = xp.compile(expression);
    }

    public Node evaluate(Source context) throws Exception {
        if (TraxUtils.isStaxSource(context)) {
            Element element = getRootElement(context);
            return (Node) xpath.evaluate(element, XPathConstants.NODE);
        } else if (context instanceof SAXSource) {
            SAXSource saxSource = (SAXSource) context;
            return (Node) xpath.evaluate(saxSource.getInputSource(), XPathConstants.NODE);
        } else if (context instanceof DOMSource) {
            DOMSource domSource = (DOMSource) context;
            return (Node) xpath.evaluate(domSource.getNode(), XPathConstants.NODE);
        } else if (context instanceof StreamSource) {
            StreamSource streamSource = (StreamSource) context;
            InputSource inputSource;
            if (streamSource.getInputStream() != null) {
                inputSource = new InputSource(streamSource.getInputStream());
            } else if (streamSource.getReader() != null) {
                inputSource = new InputSource(streamSource.getReader());
            } else {
                throw new IllegalArgumentException("StreamSource contains neither InputStream nor Reader");
            }
            return (Node) xpath.evaluate(inputSource, XPathConstants.NODE);
        } else {
            throw new IllegalArgumentException("context type unknown");
        }
    }

    protected Element getRootElement(Source source) throws TransformerException {
           DOMResult domResult = new DOMResult();
           transform(source, domResult);
           Document document = (Document) domResult.getNode();
           return document.getDocumentElement();
       }

    
}
