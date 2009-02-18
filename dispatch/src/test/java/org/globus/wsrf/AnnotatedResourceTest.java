package org.globus.wsrf;

import com.test.resource.TestResource;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;
import org.testng.annotations.Test;

import javax.xml.namespace.QName;

@Test(groups = {"resourceful", "dispatch"})
public class AnnotatedResourceTest {

    public void testSetResource() {
        AnnotatedResource resource = new AnnotatedResource(new TestResource());
        QName name = resource.getResourceKeyName();
        assertEquals(new QName("http://www.test.com", "TestKey"), name);
        try{
            new AnnotatedResource(new Object());
            fail();
        }catch(IllegalArgumentException ex){
            //this should happen
        }
    }
}
