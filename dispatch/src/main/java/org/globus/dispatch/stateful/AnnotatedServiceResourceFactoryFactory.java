package org.globus.dispatch.stateful;

import org.globus.wsrf.annotations.TerminateResource;
import org.globus.dispatch.stateful.annotations.FindResource;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;


public class AnnotatedServiceResourceFactoryFactory {
    JAXBContextFinder finder;

    public JAXBContextFinder getFinder() {
        return finder;
    }

    public void setFinder(JAXBContextFinder finder) {
        this.finder = finder;
    }

    public ReflectiveServiceResourceFactory createFactory(Object resourceFac) throws StateException{
        ReflectiveServiceResourceFactory fac = new ReflectiveServiceResourceFactory();
        fac.setDeleter(createDelete(resourceFac, TerminateResource.class));
        fac.setFinder(createDelete(resourceFac, FindResource.class));        
        return fac;
    }



    public ReflectiveMethod createDelete(Object resourceFac, Class<? extends Annotation> anno) throws StateException{
        Class<?> resourceFacClass = resourceFac.getClass();
        Method[] methods = resourceFacClass.getMethods();
        Method creationMethod = null;
        for (Method method : methods) {
            if (method.getAnnotation(anno) != null) {
                if (creationMethod != null) {
                    throw new IllegalArgumentException("Only one creation method per resource can be created");
                }
                creationMethod = method;
            }
        }
        if (creationMethod != null) {
            return new ReflectiveMethod(finder, creationMethod, resourceFac);
        }
        return null;
    }
}
