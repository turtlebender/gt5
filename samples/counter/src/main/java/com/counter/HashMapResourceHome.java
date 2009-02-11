package com.counter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class HashMapResourceHome<T,V> {
    private Map<T,V> resources = new HashMap<T,V>();

    public Collection<T> getIds(){
        return resources.keySet();
    }

    public void addResource(T id, V resource){
        this.resources.put(id, resource);
    }

    public V findResource(T id){
        return this.resources.get(id);
    }

    public void deleteResource(T id){
        this.resources.remove(id);
    }
}
