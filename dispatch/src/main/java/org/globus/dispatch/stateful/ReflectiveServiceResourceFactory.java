package org.globus.dispatch.stateful;

public class ReflectiveServiceResourceFactory implements ServiceResourceFactory<Object> {
    ReflectiveMethod finder;
    ReflectiveMethod deleter;

    public ReflectiveMethod getFinder() {
        return finder;
    }

    public void setFinder(ReflectiveMethod finder) {
        this.finder = finder;
    }

    public Object find(Object key) throws StateException{
        return finder.invoke(key);
    }

    public ReflectiveMethod getDeleter() {
        return deleter;
    }

    public void setDeleter(ReflectiveMethod deleter) {
        this.deleter = deleter;
    }

    public Object create(Object params) throws StateException{
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void delete(Object key) throws StateException{
        deleter.invoke(key);
    }
}
