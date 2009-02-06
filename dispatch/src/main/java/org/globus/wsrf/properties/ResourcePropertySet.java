package org.globus.wsrf.properties;

import javax.xml.namespace.QName;


public interface ResourcePropertySet<T, V> {
    public V getResourceProperty(T resourceKey, QName propName);
}
