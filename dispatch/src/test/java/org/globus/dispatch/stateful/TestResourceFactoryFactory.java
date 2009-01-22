package org.globus.dispatch.stateful;

import org.globus.dispatch.stateful.annotations.FindResource;
import org.globus.wsrf.annotations.TerminateResource;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


public class TestResourceFactoryFactory {
    MyResourceFactory mrf;
    ReflectiveServiceResourceFactory fac;

    @BeforeMethod
    public void setup() throws StateException{
        JAXBContextFinder finder = new JAXBContextFinder();
        AnnotatedServiceResourceFactoryFactory asr = new AnnotatedServiceResourceFactoryFactory();
        asr.setFinder(finder);
        mrf = new MyResourceFactory();
        fac = asr.createFactory(mrf);
    }

    @Test
    public void testFind() throws StateException{        
        fac.find("My source");
    }


    class MyResourceFactory{
        boolean deleted = false;

        @FindResource
        public String find(String id){
            return "Resource for " + id;
        }

        @TerminateResource
        public void delete(String id){
            deleted = true;
        }

    }
}
