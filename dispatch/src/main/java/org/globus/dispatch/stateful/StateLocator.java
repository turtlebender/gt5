package org.globus.dispatch.stateful;

public interface StateLocator<T, V> {

    public V getState(T key);

}
