package org.globus.wsrf;

import com.test.resource.TestInterface;
import com.test.resource.TestResource;
import org.globus.wsrf.properties.Resource;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.testng.annotations.Test;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: turtlebender
 * Date: Feb 16, 2009
 * Time: 4:29:27 PM
 * To change this template use File | Settings | File Templates.
 */
@Test(groups = {"resourceful", "dispatch"})
public class BeanProcessorTest {
    Mockery context = new Mockery();

    public void testProcessor() throws Exception {
        final TestResource testResource = new TestResource();
        BeanProcessor processor = new BeanProcessor();
        final ResourceDelegateFactory factory = context.mock(ResourceDelegateFactory.class);
        final TestInterface delegate = context.mock(TestInterface.class);
        context.checking(new Expectations() {{
            oneOf(factory).supports(testResource);
            will(returnValue(true));
            oneOf(factory).getDelegate(testResource);
            will(returnValue(delegate));
            oneOf(factory).getInterface();
            will(returnValue(TestInterface.class));
            oneOf(delegate).testMethod();
            will(returnValue(true));
        }});
        List<ResourceDelegateFactory> factories = new ArrayList<ResourceDelegateFactory>();
        factories.add(factory);
        processor.setDelegateFactories(factories);
        ProcessedResource pr = processor.invoke(testResource);
        Resource resource = pr.getResource();
        assertTrue(resource instanceof TestInterface);
        assertTrue(((TestInterface) resource).testMethod());
        assertTrue(((TestResource) resource).concreteTest());
    }
}
