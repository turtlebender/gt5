package org.globus.dispatch.camel;

import org.globus.wsrf.annotations.AddressingAction;
import org.globus.wsrf.annotations.StatefulResource;

@StatefulResource(keyNamespace = "http://www.test.com", keyLocalpart = "TestKey",
        resourceNamespace = "http://www.test.com", resourceLocalpart = "TestResource")
public abstract class TestResource {
    @AddressingAction(namespace = "http://www.sample.com", path = "/foo")
    public String foo(Testreq sample) {
        return "Response: " + sample;
    }
}
