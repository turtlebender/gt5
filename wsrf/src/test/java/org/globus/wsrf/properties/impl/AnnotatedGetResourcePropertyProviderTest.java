package org.globus.wsrf.properties.impl;

import org.globus.wsrf.common.SampleResource;
import org.globus.wsrf.properties.UnknownResourcePropertyException;
import org.oasis.wsrf.resourceproperties.GetResourcePropertyResponse;
import org.oasis.wsrf.resourceproperties.ObjectFactory;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;
import org.testng.annotations.Test;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

@Test(groups = {"wsrf", "properties"})
public class AnnotatedGetResourcePropertyProviderTest {
    ObjectFactory fac = new ObjectFactory();

    public void testGetResourceProperty() throws Exception {
        SampleResource resource = new SampleResource();
        AnnotatedGetResourcePropertyProvider provider = new AnnotatedGetResourcePropertyProvider(resource);
        provider.afterPropertiesSet();
        GetResourcePropertyResponse response = provider.getResourceProperty("My Resource Id",
                fac.createGetResourceProperty(new QName("http://www.sample.com", "SampleResource/StatefulState")));
        assertEquals(((JAXBElement) response.getAny().get(0)).getValue(), SampleResource.ResourceState.ACTIVE);
    }

    public void testGetRPNullResource() throws Exception {
        AnnotatedGetResourcePropertyProvider provider = new AnnotatedGetResourcePropertyProvider();
        try {
            provider.afterPropertiesSet();
            fail();
        } catch (Exception ex) {
            //should happen
        }
    }

    public void testNonResource() throws Exception {
        AnnotatedGetResourcePropertyProvider provider = new AnnotatedGetResourcePropertyProvider(new Object());
        try {
            provider.afterPropertiesSet();
            fail();
        } catch (Exception ex) {
            //should happen
        }
        provider = new AnnotatedGetResourcePropertyProvider();
        try{
            provider.getResourceProperty("foo", fac.createGetResourceProperty(new QName("http://fake.com", "FakeRP")));
            fail();
        }catch(Exception ex){
            //this should happen
        }
    }

    public void testUnknownRP() throws Exception {
        AnnotatedGetResourcePropertyProvider provider = new AnnotatedGetResourcePropertyProvider(new SampleResource());
        try {
            provider.getResourceProperty("foo", fac.createGetResourceProperty(new QName("http://fake.com", "FakeRP")));
            fail();
        } catch (UnknownResourcePropertyException ex) {
            //should happen
        }
    }

    public void testSingletonProperty() throws Exception {
        AnnotatedGetResourcePropertyProvider provider = new AnnotatedGetResourcePropertyProvider(new SampleResource());
        provider.afterPropertiesSet();
        GetResourcePropertyResponse response = provider.getResourceProperty(null,
                fac.createGetResourceProperty(new QName("http://www.sample.com", "SampleResource/SingletonState")));
        JAXBElement<?> element = (JAXBElement<?>) response.getAny().get(0);
        assertEquals(element.getValue(), SampleResource.SINGLETON_STATE);
    }
}
