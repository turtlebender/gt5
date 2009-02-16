package org.globus.resourceful.springws;

import org.globus.wsrf.annotations.AddressingAction;
import org.globus.wsrf.annotations.CreateResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.JdkVersion;
import org.springframework.ws.server.endpoint.MethodEndpoint;
import org.springframework.ws.soap.addressing.server.AbstractActionEndpointMapping;

import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;


public class ResourceEndpointMapping extends AbstractActionEndpointMapping {
    private URI address;
    private Logger logger = LoggerFactory.getLogger(getClass());

    public void setTarget(Object bean) throws Exception {
        registerMethods(bean);
    }

    @Override
    protected Object lookupEndpoint(URI action) {
        return super.lookupEndpoint(action);    //To change body of overridden methods use File | Settings | File Templates.
    }

    private void registerMethods(Object bean) throws Exception {
        Class beanClass = AopUtils.getTargetClass(bean);
        processClass(bean, beanClass);
        for (Class parent : bean.getClass().getInterfaces()) {
            processClass(bean, parent);
        }
    }

    private void processClass(Object bean, Class beanClass) throws URISyntaxException {
        Method[] methods = beanClass.getDeclaredMethods();
        for (Method method : methods) {
            if (JdkVersion.isAtLeastJava15() && method.isSynthetic() || method.getDeclaringClass().equals(Object.class)) {
                continue;
            }
            AddressingAction aa = method.getAnnotation(AddressingAction.class);
            if (aa != null) {
                URI uri = new URI(aa.value());
//                logger.debug("Registering action {} to {}", uri, method);
                if (this.lookupEndpoint(uri) == null)
                    super.registerEndpoint(uri, new MethodEndpoint(bean, method));
            }
            CreateResource cr = method.getAnnotation(CreateResource.class);
            if (cr != null) {
                URI uri = new URI(cr.value());
//                logger.debug("Registering creator action {} to {}", uri, method);
                if (this.lookupEndpoint(uri) == null)
                    super.registerEndpoint(uri, new MethodEndpoint(bean, method));
            }
        }
    }

    protected URI getEndpointAddress(Object o) {
        return address;
    }

    public void setAddress(URI address) {
        this.address = address;
    }
}
