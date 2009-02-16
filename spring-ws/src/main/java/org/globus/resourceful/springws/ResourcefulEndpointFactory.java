package org.globus.resourceful.springws;

import org.globus.common.MethodInvocationInterceptor;
import org.globus.wsrf.BeanProcessor;
import org.globus.wsrf.ProcessedResource;
import org.globus.wsrf.ResourceDelegateFactory;
import org.globus.wsrf.ResourcefulMethodInvoker;
import org.globus.wsrf.WebMethodInvoker;
import org.globus.wsrf.XPathEvaluator;
import org.globus.wsrf.lifetime.impl.AnnotatedDestroyableFactory;
import org.globus.wsrf.lifetime.impl.AnnotatedFutureDestroyableFactory;
import org.globus.wsrf.properties.impl.AnnotatedGetRPFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.server.EndpointAdapter;
import org.springframework.ws.server.EndpointMapping;
import org.springframework.ws.server.MessageDispatcher;
import org.springframework.ws.soap.server.SoapMessageDispatcher;
import org.springframework.ws.transport.WebServiceMessageSender;
import org.springframework.ws.transport.http.HttpUrlConnectionMessageSender;

import javax.xml.xpath.XPathException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourcefulEndpointFactory implements InitializingBean, FactoryBean, ApplicationContextAware {
    private Object serviceObject;
    private String jaxbContextPath;
    //    private Marshaller marshaller;
    //    private Unmarshaller unmarshaller;
    private List<MethodInvocationInterceptor> beforeInterceptors;
    private List<ResourceDelegateFactory> delegateFactories;
    private ApplicationContext appContext;
    private String name;
    private URI address;
    private MessageDispatcher dispatcher;


    public URI getAddress() {
        return address;
    }

    public void setAddress(URI address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getServiceObject() {
        return serviceObject;
    }

    public void setServiceObject(Object serviceObject) {
        this.serviceObject = serviceObject;
    }

    public List<MethodInvocationInterceptor> getBeforeInterceptors() {
        return beforeInterceptors;
    }

    public void setBeforeInterceptors(List<MethodInvocationInterceptor> beforeInterceptors) {
        this.beforeInterceptors = beforeInterceptors;
    }

    public void setDelegateFactories(List<ResourceDelegateFactory> delegateFactories) {
        this.delegateFactories = delegateFactories;
    }


    public void setJaxbContextPath(String jaxbContextPath) {
        this.jaxbContextPath = jaxbContextPath;
    }

    private EndpointAdapter createAdapter(ProcessedResource resource) throws Exception {
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
        WebMethodInvoker wmi = createInvoker(resource, tmp, tmp);
        return createAdapter(wmi, tmp, tmp);

    }

    private void addDefaultDelegateFactories(BeanProcessor processor) {
        if (delegateFactories == null) {
            delegateFactories = new ArrayList<ResourceDelegateFactory>();
            processor.setDelegateFactories(delegateFactories);
        }
        delegateFactories.add(new AnnotatedGetRPFactory());
        delegateFactories.add(new AnnotatedFutureDestroyableFactory());
        delegateFactories.add(new AnnotatedDestroyableFactory());
        processor.setDelegateFactories(this.delegateFactories);
    }

    public Object getObject() throws Exception {
        return dispatcher;
    }

    private ResourceEndpointMapping createMapping(Object resource) throws Exception {
        ResourceEndpointMapping mapping = new ResourceEndpointMapping();
        mapping.setAddress(address);
        mapping.setMessageSenders(new WebServiceMessageSender[]{new HttpUrlConnectionMessageSender()});
        mapping.setTarget(resource);
//        mapping.registerMethods(resource);
        mapping.afterPropertiesSet();
        return mapping;
    }


    public Class getObjectType() {
        return MessageDispatcher.class;
    }

    public boolean isSingleton() {
        return true;
    }


    private EndpointAdapter createAdapter(WebMethodInvoker invoker, Marshaller marshaller,
                                          Unmarshaller unmarshaller) throws Exception {
        ResourcefulMethodEndpointAdapter adapter = new ResourcefulMethodEndpointAdapter();
        adapter.setInvoker(invoker);
        adapter.setMarshaller(marshaller);
        adapter.setUnmarshaller(unmarshaller);
        adapter.afterPropertiesSet();
        return adapter;
    }

    private WebMethodInvoker createInvoker(ProcessedResource resource, Marshaller marshaller, Unmarshaller unmarshaller) {
        WebMethodInvoker invoker = new WebMethodInvoker();
        ResourcefulMethodInvoker rmi = new ResourcefulMethodInvoker();
        rmi.setInterceptors(this.beforeInterceptors);
        invoker.setRmi(rmi);
        invoker.setUnmarshaller(unmarshaller);
        invoker.setMarshaller(marshaller);
        XPathEvaluator evaluator = new XPathEvaluator();
        try {
            evaluator.setQName(resource.getQname());
        } catch (XPathException e) {
            throw new FatalBeanException("Unable to create XPathEvaluator", e);
        }
        invoker.setXpression(evaluator);
        return invoker;
    }

    private ProcessedResource processBean(BeanProcessor processor) {
        ProcessedResource resource;
        try {
            resource = processor.invoke(serviceObject);
        } catch (Exception e) {
            throw new FatalBeanException("Unable to process resource", e);
        }
        return resource;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.appContext = applicationContext;
    }

    public void afterPropertiesSet() throws Exception {
        BeanProcessor processor = new BeanProcessor();
        addDefaultDelegateFactories(processor);
        SoapMessageDispatcher dispatcher = new SoapMessageDispatcher();
//        dispatcher.setBeanName(this.name);
        dispatcher.setApplicationContext(this.appContext);                
        ProcessedResource resource = processBean(processor);
        List<EndpointAdapter> adapters = new ArrayList<EndpointAdapter>();
        adapters.add(createAdapter(resource));
        dispatcher.setEndpointAdapters(adapters);
        List<EndpointMapping> mappings = new ArrayList<EndpointMapping>();
        ResourceEndpointMapping mapping = createMapping(resource.getResource());
        mapping.afterPropertiesSet();
        mapping.setApplicationContext(this.appContext);        
        mappings.add(mapping);
        dispatcher.setEndpointMappings(mappings);
        this.dispatcher = dispatcher;
    }
}
