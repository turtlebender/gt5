package org.globus.wsrf.properties.impl;

import org.globus.wsrf.common.SampleResource;
import org.globus.wsrf.properties.GetResourcePropertyProvider;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

import java.util.ArrayList;


public class AnnotatedGetRPFactoryTest {
    @Test(groups = {"wsrf", "properties"})
    public void testGetDelegate() {
        AnnotatedGetRPFactory fac = new AnnotatedGetRPFactory();
        assertEquals(GetResourcePropertyProvider.class, fac.getInterface());
        assertTrue(fac.supports(new SampleResource()));
        assertFalse(fac.supports(new ArrayList()));
        assertNotNull(fac.getDelegate(new SampleResource()));        
    }
}
