package org.globus.dispatch.stateful;

public interface ServiceResourceFactory<T> {

    public Object find(T key) throws StateException;

    public Object create(T params) throws StateException;

    public void delete(T key) throws StateException;

}
