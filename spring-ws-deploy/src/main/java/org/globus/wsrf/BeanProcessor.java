package org.globus.wsrf;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.globus.wsrf.annotations.StatefulResource;
import org.globus.wsrf.properties.Resource;
import org.globus.wsrf.properties.ResourceDelegateFactory;
import org.globus.wsrf.properties.impl.AnnotatedResource;
import org.springframework.beans.factory.InitializingBean;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BeanProcessor implements InitializingBean {
    private List<ResourceDelegateFactory> delegateFactories;
    private Set<Class> delegateInterfaces = new HashSet<Class>();

    public void afterPropertiesSet() throws Exception {

    }

    public List<ResourceDelegateFactory> getDelegateFactories() {
        return delegateFactories;
    }

    public void setDelegateFactories(List<ResourceDelegateFactory> delegateFactories) {
        this.delegateFactories = delegateFactories;
    }

    class DelegatingHandler implements MethodInterceptor {
        List<Class> interfaces;
        List<Object> delegates;
        Object parent;

        public DelegatingHandler(Object parent, List<Class> interfaces, List<Object> delegates) {
            this.interfaces = interfaces;
            this.delegates = delegates;
            this.parent = parent;
        }

        public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            Class declaringClass = method.getDeclaringClass();
            for (int i = 0; i < interfaces.size(); i++) {
                if (declaringClass.isAssignableFrom(interfaces.get(i))) {
                    try {
                        return methodProxy.invoke(delegates.get(i), args);
                    } catch (InvocationTargetException e) {
                        throw e.getTargetException();
                    }
                }
            }
            return methodProxy.invoke(parent, args);
        }
    }

    public ProcessedResource invoke(Object bean) throws Exception {
        StatefulResource sr = bean.getClass().getAnnotation(StatefulResource.class);
        if (sr != null) {
            if (delegateFactories == null) {
                delegateFactories = new ArrayList<ResourceDelegateFactory>();
            }
            addDefaultDelegateFactories(delegateFactories);
            List<Object> delegate = new ArrayList<Object>();
            List<Class> interfaces = new ArrayList<Class>();
            AnnotatedResource ar = new AnnotatedResource(bean);
            delegate.add(ar);
            interfaces.add(Resource.class);
            this.delegateInterfaces.add(Resource.class);
            for (ResourceDelegateFactory fac : delegateFactories) {
                if (fac.supports(bean)) {
                    if (!delegateInterfaces.contains(fac.getInterface())) {
                        delegate.add(fac.getDelegate(bean));
                        interfaces.add(fac.getInterface());
                    }
                }
            }
            DelegatingHandler handler = new DelegatingHandler(bean, interfaces, delegate);
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(bean.getClass());
            enhancer.setInterfaces(interfaces.toArray(new Class[interfaces.size()]));
            enhancer.setCallback(handler);
            Resource resource = (Resource) enhancer.create();
            XPathEvaluator evaluator = new XPathEvaluator();
            evaluator.setQName(resource.getResourceKeyName());
            return new ProcessedResource(resource, evaluator);
        } else if (bean instanceof Resource) {
            Resource resource = (Resource) bean;
            XPathEvaluator evaluator = new XPathEvaluator();
            evaluator.setQName(resource.getResourceKeyName());
            return new ProcessedResource(resource, evaluator);
        } else return null;
    }

    private void addDefaultDelegateFactories(List<ResourceDelegateFactory> delegateFactories) {
        delegateFactories.add(new DefaultGetRPProviderFactory());
    }

}
