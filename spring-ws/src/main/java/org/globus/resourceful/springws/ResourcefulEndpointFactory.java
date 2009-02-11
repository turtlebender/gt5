package org.globus.resourceful.springws;

import org.globus.wsrf.BeanProcessor;
import org.globus.wsrf.ProcessedResource;
import org.globus.wsrf.ResourceDelegateFactory;
import org.globus.wsrf.WebMethodInvoker;
import org.globus.common.MethodInvocationInterceptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourcefulEndpointFactory implements FactoryBean, BeanPostProcessor {
    boolean resourcePopulated = false;
    private ResourcefulMethodEndpointAdapter adapter;
    private String jaxbContextPath;
    private Marshaller marshaller;
    private Unmarshaller unmarshaller;
    private BeanProcessor processor = new BeanProcessor();
    private List<MethodInvocationInterceptor> beforeInterceptors;

    public List<MethodInvocationInterceptor> getBeforeInterceptors() {
        return beforeInterceptors;
    }

    public void setBeforeInterceptors(List<MethodInvocationInterceptor> beforeInterceptors) {
        this.beforeInterceptors = beforeInterceptors;
    }

    public void setDelegateFactories(List<ResourceDelegateFactory> delegateFactories) {
        this.processor.setDelegateFactories(delegateFactories);
    }

    public Marshaller getMarshaller() {
        return marshaller;
    }


    public Unmarshaller getUnmarshaller() {
        return unmarshaller;
    }

    public void setJaxbContextPath(String jaxbContextPath) {
        this.jaxbContextPath = jaxbContextPath;
    }

    private void instantiateMarshallers(String jaxbContextPath) throws Exception{
        String[] pathElements = jaxbContextPath.split(":");
        Map<String, Boolean> factoryPaths = new HashMap<String, Boolean>();
        factoryPaths.put("org.oasis.wsrf.resourceproperties", false);
        factoryPaths.put("org.oasis.wsrf.lifetime", false);
        for (String pathElement : pathElements) {
            if (factoryPaths.containsKey(pathElement)) {
                factoryPaths.put(pathElement, true);
            }
        }
        StringBuilder builder = new StringBuilder(jaxbContextPath);
        for (Map.Entry<String, Boolean> factoryPath : factoryPaths.entrySet()) {
            if (!factoryPath.getValue()) {
                builder.append(":");
                builder.append(factoryPath.getKey());
            }
        }
        Jaxb2Marshaller tmp = new Jaxb2Marshaller();
        tmp.setContextPath(builder.toString());
        tmp.afterPropertiesSet();
        this.marshaller = tmp;
        this.unmarshaller = tmp;
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (resourcePopulated) {
            return bean;
        }        
        ProcessedResource resource;
        try {
            resource = processor.invoke(bean);
        } catch (Exception e) {
            throw new FatalBeanException("Unable to process resource", e);
        }

        if (resource != null) {
            try {
                instantiateMarshallers(jaxbContextPath);
            } catch (Exception e) {
                throw new FatalBeanException("Unable to create JAXB context", e);
            }
            resourcePopulated = true;
            WebMethodInvoker invoker = new WebMethodInvoker();
            invoker.setUnmarshaller(this.unmarshaller);
            invoker.setMarshaller(this.marshaller);
            invoker.setXpression(resource.getEvaluator());
            invoker.setInterceptors(this.beforeInterceptors);
            adapter.setInvoker(invoker);
            adapter.setMarshaller(this.marshaller);
            adapter.setUnmarshaller(this.unmarshaller);
            return resource.getResource();
        }
        return bean;
    }


    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    public Object getObject() throws Exception {
        adapter = new ResourcefulMethodEndpointAdapter();
        adapter.setMarshaller(this.marshaller);
        adapter.setUnmarshaller(this.unmarshaller);
        return adapter;
    }


    public Class getObjectType() {
        return ResourcefulMethodEndpointAdapter.class;
    }

    public boolean isSingleton() {
        return true;
    }

}
