package org.globus.aop;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.AssertJUnit.*;


public class ProxyTest {
    SampleInterceptor interceptor1;
    SampleInterceptor interceptor2;
    SampleProxied proxied;

    @BeforeMethod
    public void setup() {
        interceptor1 = new SampleInterceptor1("Sample1");
        interceptor2 = new SampleInterceptor2("Sample2");
        ProxyCreator<SampleProxied> fac = new ProxyCreator<SampleProxied>();
        fac.registerInterceptor(new MethodFilter1(), interceptor1);
        fac.registerInterceptor(new MethodFilter2(), interceptor2);
        SampleProxied target = new SampleProxied();
        proxied = fac.createProxy(target);
    }

    @Test
    public void test1() {
        proxied.test1();
        assertTrue(interceptor1.before);
        assertFalse(interceptor1.after);
        assertFalse(interceptor1.afterFinally);
        assertFalse(interceptor1.afterThrows);
        assertFalse(interceptor2.before);
        assertFalse(interceptor2.after);
        assertFalse(interceptor2.afterFinally);
        assertFalse(interceptor2.afterThrows);
    }

    @Test
    public void test3() {
        proxied.test3();
        assertTrue(interceptor1.before);
        assertFalse(interceptor1.after);
        assertFalse(interceptor1.afterFinally);
        assertFalse(interceptor1.afterThrows);
        assertTrue(interceptor2.before);
        assertFalse(interceptor2.after);
        assertFalse(interceptor2.afterFinally);
        assertFalse(interceptor2.afterThrows);
    }

    @Test
    public void test5() {
        try {
            proxied.test5();
            fail();
        } catch (Exception ex) {
            //this should happen
        }
        assertFalse(interceptor1.before);
        assertFalse(interceptor1.after);
        assertFalse(interceptor1.afterFinally);
        assertTrue(interceptor1.afterThrows);
        assertFalse(interceptor2.before);
        assertFalse(interceptor2.after);
        assertFalse(interceptor2.afterFinally);
        assertTrue(interceptor2.afterThrows);
    }

}
