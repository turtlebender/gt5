package org.globus.resourceful.springws;

import org.globus.wsrf.annotations.AddressingAction;
import org.globus.wsrf.annotations.CreateResource;
import org.globus.wsrf.annotations.StatefulResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.JdkVersion;
import org.springframework.ws.server.endpoint.MethodEndpoint;
import org.springframework.ws.soap.addressing.server.AbstractActionEndpointMapping;

import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;


public class ResourceEndpointMapping extends AbstractActionEndpointMapping implements BeanPostProcessor {
    private URI address;
    private Logger logger = LoggerFactory.getLogger(getClass());

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    private void registerMethods(Object bean) throws Exception {
        Class beanClass = AopUtils.getTargetClass(bean);
        processClass(bean, beanClass);
        for (Class parent : bean.getClass().getInterfaces()) {
            processClass(bean, parent);
        }
    }

    private void processClass(Object bean, Class beanClass) throws URISyntaxException {
        for (Method method : beanClass.getDeclaredMethods()) {
            if (JdkVersion.isAtLeastJava15() && method.isSynthetic() || method.getDeclaringClass().equals(Object.class)) {
                continue;
            }
            AddressingAction aa = method.getAnnotation(AddressingAction.class);
            if (aa != null) {
                URI uri = new URI(aa.value());
                logger.debug("Registering action {} to {}", uri, method);
                super.registerEndpoint(uri, new MethodEndpoint(bean, method));                
            }
            CreateResource cr = method.getAnnotation(CreateResource.class);
            if (cr != null) {
                URI uri = new URI(cr.value());
                logger.debug("Registering creator action {} to {}", uri, method);
                super.registerEndpoint(uri, new MethodEndpoint(bean, method));
            }
        }
    }

    @SuppressWarnings("unchecked")
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class targetClass = AopUtils.getTargetClass(bean);
        if (targetClass.getAnnotation(StatefulResource.class) != null) {
            try {
                registerMethods(bean);
            } catch (Exception e) {
                throw new FatalBeanException("Error registering action methods", e);
            }
        }
        return bean;
    }

    protected URI getEndpointAddress(Object o) {
        return address;
    }

    public void setAddress(URI address) {
        this.address = address;
    }
}
