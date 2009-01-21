package org.globus.dispatch.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;

public class Converter {

    private static final Class DOM_TO_SAX_CLASS;
    private static Logger logger = LoggerFactory.getLogger(Converter.class);

    static {
        // TODO: Use ObjectHelper.loadClass instead
        Class cl = null;
        try {
            cl = Class.forName("org.apache.xalan.xsltc.trax.DOM2SAX");
        } catch (Throwable t) {
            // do nothing here
        }
        DOM_TO_SAX_CLASS = cl;
    }

    public static String getNamespace(String prefix, Node e, Node stopNode) {
        while (e != null && (e.getNodeType() == Node.ELEMENT_NODE)) {
            Attr attr;
            if (prefix == null) {
                attr = ((Element) e).getAttributeNode("xmlns");
            } else {
                attr = ((Element) e).getAttributeNodeNS("http://www.w3.org/2000/xmlns/", prefix);
            }
            if (attr != null) return attr.getValue();
            if (e == stopNode)
                return null;
            e = e.getParentNode();
        }
        return null;
    }


    public static QName getFullQNameFromString(String str, Node e) {
        return getQNameFromString(str, e, true);
    }

    private static QName getQNameFromString(String str, Node e, boolean defaultNS) {
        if (str == null || e == null)
            return null;

        int idx = str.indexOf(':');
        if (idx > -1) {
            String prefix = str.substring(0, idx);
            String ns = getNamespace(prefix, e, null);
            if (ns == null) {
                logger.warn("Cannot obtain namespaceURI for prefix: " + prefix);
                return null;
            }
            return new QName(ns, str.substring(idx + 1));
        } else {
            if (defaultNS) {
                String ns = getNamespace(null, e, null);
                if (ns != null)
                    return new QName(ns, str);
            }
            return new QName("", str);
        }
    }

    public static String getPrefix(String uri, Node e) {
        while (e != null && (e.getNodeType() == Element.ELEMENT_NODE)) {
            NamedNodeMap attrs = e.getAttributes();
            for (int n = 0; n < attrs.getLength(); n++) {
                Attr a = (Attr) attrs.item(n);
                String name;
                if ((name = a.getName()).startsWith("xmlns:") &&
                        a.getNodeValue().equals(uri)) {
                    return name.substring(6);
                }
            }
            e = e.getParentNode();
        }
        return null;
    }


    public static StreamSource toStreamSource(Source source) throws TransformerException {
        if (source instanceof StreamSource) {
            return (StreamSource) source;
        } else if (source instanceof DOMSource) {
            return toStreamSourceFromDOM((DOMSource) source);
        } else if (source instanceof SAXSource) {
            return toStreamSourceFromSAX((SAXSource) source);
        } else {
            return null;
        }
    }

    public static StreamSource toStreamSourceFromSAX(SAXSource source) throws TransformerException {
        InputSource inputSource = source.getInputSource();
        if (inputSource != null) {
            if (inputSource.getCharacterStream() != null) {
                return new StreamSource(inputSource.getCharacterStream());
            }
            if (inputSource.getByteStream() != null) {
                return new StreamSource(inputSource.getByteStream());
            }
        }
        String result = toString(source);
        return new StringSource(result);
    }

    public static StreamSource toStreamSourceFromDOM(DOMSource source) throws TransformerException {
        String result = toString(source);
        return new StringSource(result);
    }

    public static String toString(Source source) throws TransformerException {
        if (source == null) {
            return null;
        } else if (source instanceof StringSource) {
            return ((StringSource) source).getText();
        } else if (source instanceof BytesSource) {
            return new String(((BytesSource) source).getData());
        } else {
            StringWriter buffer = new StringWriter();
            toResult(source, new StreamResult(buffer));
            return buffer.toString();
        }
    }

    /**
     * Converts the given input Source into the required result
     */
    public static void toResult(Source source, Result result) throws TransformerException {
        if (source == null) {
            return;
        }

        Transformer transformer = createTransfomer();
        if (transformer == null) {
            throw new TransformerException("Could not create a transformer - JAXP is misconfigured!");
        }
        transformer.transform(source, result);
    }

    public static DOMSource toDOMSource(Source source) throws ParserConfigurationException, IOException, SAXException,
            TransformerException {
        if (source instanceof DOMSource) {
            return (DOMSource) source;
        } else if (source instanceof SAXSource) {
            return toDOMSourceFromSAX((SAXSource) source);
        } else if (source instanceof StreamSource) {
            return toDOMSourceFromStream((StreamSource) source);
        } else {
            return null;
        }
    }

    public static DOMSource toDOMSourceFromSAX(SAXSource source) throws IOException, SAXException, ParserConfigurationException, TransformerException {
        return new DOMSource(toDOMNodeFromSAX(source));
    }

    public static Node toDOMNodeFromSAX(SAXSource source) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        DOMResult result = new DOMResult();
        toResult(source, result);
        return result.getNode();
    }


    public static DOMSource toDOMSourceFromStream(StreamSource source) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder builder = createDocumentBuilder();
        String systemId = source.getSystemId();
        Document document;
        Reader reader = source.getReader();
        if (reader != null) {
            document = builder.parse(new InputSource(reader));
        } else {
            InputStream inputStream = source.getInputStream();
            if (inputStream != null) {
                InputSource inputsource = new InputSource(inputStream);
                inputsource.setSystemId(systemId);
                document = builder.parse(inputsource);
            } else {
                throw new IOException("No input stream or reader available");
            }
        }
        return new DOMSource(document, systemId);
    }

    public static SAXSource toSAXSource(String source) throws IOException, SAXException, TransformerException {
        return toSAXSource(toSource(source));
    }

    public static StringSource toSource(String data) {
        return new StringSource(data);
    }

    public static SAXSource toSAXSource(InputStream source) throws IOException, SAXException, TransformerException {
        return toSAXSource(toStreamSource(source));
    }

    public static StreamSource toStreamSource(InputStream in) throws TransformerException {
        if (in != null) {
            return new StreamSource(in);
        }
        return null;
    }

    public static SAXSource toSAXSource(Source source) throws IOException, SAXException, TransformerException {
        if (source instanceof SAXSource) {
            return (SAXSource) source;
        } else if (source instanceof DOMSource) {
            return toSAXSourceFromDOM((DOMSource) source);
        } else if (source instanceof StreamSource) {
            return toSAXSourceFromStream((StreamSource) source);
        } else {
            return null;
        }
    }

    public static SAXSource toSAXSourceFromStream(StreamSource source) {
        InputSource inputSource;
        if (source.getReader() != null) {
            inputSource = new InputSource(source.getReader());
        } else {
            inputSource = new InputSource(source.getInputStream());
        }
        inputSource.setSystemId(source.getSystemId());
        inputSource.setPublicId(source.getPublicId());
        return new SAXSource(inputSource);
    }

    public static SAXSource toSAXSourceFromDOM(DOMSource source) throws TransformerException {
        if (DOM_TO_SAX_CLASS != null) {
            try {
                Constructor cns = DOM_TO_SAX_CLASS.getConstructor(Node.class);
                XMLReader converter = (XMLReader) cns.newInstance(source.getNode());
                return new SAXSource(converter, new InputSource());
            } catch (Exception e) {
                throw new TransformerException(e);
            }
        } else {
            String str = toString(source);
            StringReader reader = new StringReader(str);
            return new SAXSource(new InputSource(reader));
        }
    }

    private static DocumentBuilderFactory documentBuilderFactory;

    public static DocumentBuilderFactory getDocumentBuilderFactory() {
        if (documentBuilderFactory == null) {
            documentBuilderFactory = createDocumentBuilderFactory();
        }
        return documentBuilderFactory;
    }

    public static DocumentBuilderFactory createDocumentBuilderFactory() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setIgnoringElementContentWhitespace(true);
        factory.setIgnoringComments(true);
        return factory;
    }


    public static DocumentBuilder createDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory factory = getDocumentBuilderFactory();
        return factory.newDocumentBuilder();
    }

    private static Transformer createTransfomer() throws TransformerException {
        return getFactory().newTransformer();
    }

    private static TransformerFactory factory;

    private static TransformerFactory getFactory() {
        if (factory == null) {
            factory = TransformerFactory.newInstance();
        }
        return factory;
    }

    static class BytesSource extends StreamSource {
        private byte[] data;

        public BytesSource(byte[] data) {
            if (data == null) {
                throw new IllegalArgumentException("Data must be set");
            }
            this.data = data;
        }

        public BytesSource(byte[] data, String systemId) {
            if (data == null) {
                throw new IllegalArgumentException("Data must be set");
            }
            this.data = data;
            setSystemId(systemId);
        }

        public InputStream getInputStream() {
            return new ByteArrayInputStream(data);
        }

        public Reader getReader() {
            return new InputStreamReader(getInputStream());
        }

        public byte[] getData() {
            return data;
        }

        public String toString() {
            return "BytesSource[" + new String(data) + "]";
        }
    }

    static class StringSource extends StreamSource implements Externalizable {
        private String text;
        private String encoding = "UTF-8";

        public StringSource() {
        }

        public StringSource(String text) {
            if (text == null)
                throw new IllegalArgumentException("text must be set");
            this.text = text;
        }

        public StringSource(String text, String systemId) {
            this(text);
            if (systemId == null)
                throw new IllegalArgumentException("systemId must be set");
            setSystemId(systemId);
        }

        public StringSource(String text, String systemId, String encoding) {
            this(text, systemId);
            if (encoding == null) {
                throw new IllegalArgumentException("encoding must be set");
            }
            this.encoding = encoding;
        }

        public InputStream getInputStream() {
            try {
                return new ByteArrayInputStream(text.getBytes(encoding));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }

        public Reader getReader() {
            return new StringReader(text);
        }

        public String toString() {
            return "StringSource[" + text + "]";
        }

        public String getText() {
            return text;
        }

        public String getEncoding() {
            return encoding;
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeUTF(text);
            out.writeUTF(encoding);
            out.writeUTF(getPublicId());
            out.writeUTF(getSystemId());
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            text = in.readUTF();
            encoding = in.readUTF();
            setPublicId(in.readUTF());
            setSystemId(in.readUTF());
        }
    }

}
