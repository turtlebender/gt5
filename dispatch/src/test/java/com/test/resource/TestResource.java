package com.test.resource;

import org.globus.wsrf.annotations.StatefulResource;

@StatefulResource(keyLocalpart = "TestKey", keyNamespace = "http://www.test.com", resourceLocalpart = "TestResource",
        resourceNamespace = "http://www.test.com")
public class TestResource {
    public boolean concreteTest() {
        return true;
    }
}
