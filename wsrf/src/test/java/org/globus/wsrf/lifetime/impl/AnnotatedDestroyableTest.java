package org.globus.wsrf.lifetime.impl;

import org.globus.wsrf.lifetime.Destroyable;
import org.globus.wsrf.common.SampleResource;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

import java.util.ArrayList;

@Test(groups = {"lifetimeTests", "wsrf"})
public class AnnotatedDestroyableTest {

    public void testDestroy() throws Exception {
        SampleResource resource = new SampleResource();
        AnnotatedDestroyable destroyable = new AnnotatedDestroyable();
        destroyable.setResource(resource);
        destroyable.init();
        assertEquals(resource.getState("My Id"), SampleResource.ResourceState.ACTIVE);
        destroyable.destroy("Sample Resource");
        assertEquals(resource.getState("My Id"), SampleResource.ResourceState.DESTROYED);

    }

    public void testDestroyWithNoResource() throws Exception {
        AnnotatedDestroyable destroyable = new AnnotatedDestroyable();
        try {
            destroyable.init();
            fail();
        } catch (Exception e) {
            //This should happen
        }
    }

    public void testDestroyWithConstructorResource() throws Exception {
        SampleResource resource = new SampleResource();
        AnnotatedDestroyable destroyable = new AnnotatedDestroyable(resource);
        assertEquals(resource.getState("My Id"), SampleResource.ResourceState.ACTIVE);
        destroyable.init();
        destroyable.destroy("Sample Resource");
        assertEquals(resource.getState("My Id"), SampleResource.ResourceState.DESTROYED);

    }

    public void testDestroyableFactory() throws Exception {
        AnnotatedDestroyableFactory factory = new AnnotatedDestroyableFactory();
        assertEquals(factory.getInterface(), Destroyable.class);
        assertTrue(factory.supports(new SampleResource()));
        SampleResource resource = new SampleResource();
        Destroyable destroyable = factory.getDelegate(resource);
        assertEquals(resource.getState("My Id"), SampleResource.ResourceState.ACTIVE);
        destroyable.destroy("Sample Resource");
        assertEquals(resource.getState("My Id"), SampleResource.ResourceState.DESTROYED);
    }

    public void testDestroyFactoryInvalidTarget() throws Exception {
        AnnotatedDestroyableFactory factory = new AnnotatedDestroyableFactory();
        assertEquals(factory.getInterface(), Destroyable.class);
        assertFalse(factory.supports(new ArrayList()));
    }
}
