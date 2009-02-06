package org.globus.wsrf;

import org.globus.wsrf.properties.ResourceDelegateFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;

import java.util.List;

public class ResourcefulEndpointFactory implements FactoryBean, BeanPostProcessor {
    boolean resourcePopulated = false;
    private ResourcefulMethodEndpointAdapter adapter;
    private Marshaller marshaller;
    private Unmarshaller unmarshaller;
    private BeanProcessor processor = new BeanProcessor();

    public void setDelegateFactories(List<ResourceDelegateFactory> delegateFactories) {
        this.processor.setDelegateFactories(delegateFactories);
    }

    public Marshaller getMarshaller() {
        return marshaller;
    }

    public void setMarshaller(Marshaller marshaller) {
        this.marshaller = marshaller;
    }

    public Unmarshaller getUnmarshaller() {
        return unmarshaller;
    }

    public void setUnmarshaller(Unmarshaller unmarshaller) {
        this.unmarshaller = unmarshaller;
    }


    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if(resourcePopulated)
            return bean;
        try {
            processor.afterPropertiesSet();
        } catch (Exception e) {
            throw new FatalBeanException("Unable to initialize BeanProcessor", e);
        }
        if(resourcePopulated){
            return bean;
        }
        ProcessedResource resource;
        try {
            resource = processor.invoke(bean);
        } catch (Exception e) {
            throw new FatalBeanException("Unable to process resource", e);
        }

        if (resource != null) {
            resourcePopulated = true;
            WebMethodInvoker invoker = new WebMethodInvoker();
            invoker.setUnmarshaller(this.unmarshaller);
            invoker.setMarshaller(this.marshaller);
            invoker.setXpression(resource.getEvaluator());
            adapter.setInvoker(invoker);
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
