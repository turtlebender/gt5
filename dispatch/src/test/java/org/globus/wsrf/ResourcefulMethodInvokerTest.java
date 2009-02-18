package org.globus.wsrf;

import org.globus.common.MethodInvocationInterceptor;
import org.globus.common.PropertyHolder;
import org.jmock.Expectations;
import org.jmock.Mockery;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ResourcefulMethodInvokerTest {
    enum MethodResult {
        RI1, RI2, RI3, RI4, RI5
    }

    public MethodResult resourcefulIndex(@Resourceful String id) {
        return MethodResult.RI1;
    }

    public MethodResult resourcefulIndex2(Object o, @Resourceful String id) {
        return MethodResult.RI2;
    }

    public MethodResult resourcefulIndex3(@Resourceful String id, Object o) {
        return MethodResult.RI3;
    }

    public MethodResult resourcefulIndex4() {
        return MethodResult.RI4;
    }

    public MethodResult resourcefulIndex5(Object o) {
        return MethodResult.RI5;
    }


    @Test(groups = {"resourceful", "dispatch"})

    public void testGetResourcefulIndex() throws Exception {
        ResourcefulMethodInvoker rmi = new ResourcefulMethodInvoker();
        int index = rmi.getResourcefulIndex(getClass().getMethod("resourcefulIndex", String.class));
        assertEquals(0, index);
        index = rmi.getResourcefulIndex(getClass().getMethod("resourcefulIndex2", Object.class, String.class));
        assertEquals(1, index);
        index = rmi.getResourcefulIndex(getClass().getMethod("resourcefulIndex3", String.class, Object.class));
        assertEquals(0, index);
        index = rmi.getResourcefulIndex(getClass().getMethod("resourcefulIndex4"));
        assertEquals(-1, index);
    }

    @Test(groups = {"resourceful", "dispatch"})
    public void testInvoke() throws Exception {
        Mockery context = new Mockery();
        final MethodInvocationInterceptor interceptor = context.mock(MethodInvocationInterceptor.class);
        final PropertyHolder holder = new PropertyHolder() {
            public void setProperty(String name, Object value) {
            }

            public Object getProperty(String name) {
                return null;
            }
        };
        context.checking(new Expectations() {{
            exactly(5).of(interceptor).intercept(with(any(PropertyHolder.class)), with(any(Method.class)), with(anything()));
        }});
        ResourcefulMethodInvoker rmi = new ResourcefulMethodInvoker();
        List<MethodInvocationInterceptor> interceptors = new ArrayList<MethodInvocationInterceptor>();
        interceptors.add(interceptor);
        rmi.setInterceptors(interceptors);
        Method method = getClass().getMethod("resourcefulIndex", String.class);
        InvokeMethodRequest request = new InvokeMethodRequest(this, null, holder, "My Id", method);
        assertEquals(rmi.invokeMethod(request), MethodResult.RI1);
        method = getClass().getMethod("resourcefulIndex2", Object.class, String.class);
        request = new InvokeMethodRequest(this, "hello", holder, "My Id", method);
        assertEquals(rmi.invokeMethod(request), MethodResult.RI2);
        method = getClass().getMethod("resourcefulIndex3", String.class, Object.class);
        request = new InvokeMethodRequest(this, "hello", holder, "MyId", method);
        assertEquals(rmi.invokeMethod(request), MethodResult.RI3);
        method = getClass().getMethod("resourcefulIndex4");
        request = new InvokeMethodRequest(this, null, holder, null, method);
        assertEquals(rmi.invokeMethod(request), MethodResult.RI4);
        method = getClass().getMethod("resourcefulIndex5", Object.class);
        request = new InvokeMethodRequest(this, "hello", holder, null, method);
        assertEquals(rmi.invokeMethod(request), MethodResult.RI5);
    }
}
