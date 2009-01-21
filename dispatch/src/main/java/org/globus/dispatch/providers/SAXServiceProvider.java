package org.globus.dispatch.providers;

import org.globus.dispatch.DispatchException;
import static org.globus.dispatch.utils.Converter.toSAXSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXSource;
import java.io.IOException;

/**
 * This is a super class for service developers who want to work with a SAXSource directly.
 *
 * @param <T> The Type of the DefaultHandler {@link DefaultHandler} that will be used to process the request.
 */
public abstract class SAXServiceProvider<T extends DefaultHandler> implements ServiceProvider<SAXSource> {
    SAXParserFactory factory = SAXParserFactory.newInstance();

    public SAXSource invoke(SAXSource source) throws DispatchException {
        try {
            SAXParser parser = factory.newSAXParser();
            T handler = createHandler();
            parser.parse(source.getInputSource(), handler);
            return toSAXSource(getResponse(handler));
        } catch (ParserConfigurationException e) {
            throw new DispatchException("Error processing ServiceProvider", e);
        } catch (SAXException e) {
            throw new DispatchException("Error processing ServiceProvider", e);
        } catch (IOException e) {
            throw new DispatchException("Error processing ServiceProvider", e);
        } catch (TransformerException e) {
            throw new DispatchException("Error converting response to SAXSource", e);
        }
    }

    /**
     * This method is used to create the Handler to process the incoming SAXSource.  Any state stored in the handler
     * will be passed to the getResponse() method.
     *
     * @return The Handler used to process the Request.
     */
    protected abstract T createHandler();

    /**
     * This method should create any response required from the ServicePRovider.  The passed handler will contain any
     * state created during the processing of the request.  The source will be converted to a SAXSource if it is not
     * already a SAXSource.
     *
     * @param handler The hanlder used to process the Request
     * @return The response to the request as a Source
     */
    protected abstract Source getResponse(T handler);
}
