package com.counter;

import org.globus.wsrf.Resourceful;
import org.globus.wsrf.annotations.CreateResource;
import org.globus.wsrf.annotations.GetResourceProperty;
import org.globus.wsrf.annotations.StatefulResource;
import org.globus.wsrf.annotations.TerminateResource;
import org.globus.wsrf.counter.CreateCounterRequest;
import org.globus.wsrf.counter.ObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import javax.xml.bind.JAXBElement;
import java.math.BigInteger;
import java.util.UUID;

@StatefulResource(keyNamespace = "http://counter.com/CounterService", keyLocalpart = "CounterId",
        resourceNamespace = "http://counter.com/CounterService", resourceLocalpart = "Counter")
public class CounterResource implements InitializingBean {
    private HashMapResourceHome<String, BigInteger> counterMap;
    private Logger logger = LoggerFactory.getLogger(getClass());
    private ObjectFactory fac = new ObjectFactory();

    public void afterPropertiesSet() throws Exception {
        if (counterMap == null) {
            counterMap = new HashMapResourceHome<String, BigInteger>();
        }
        counterMap.addResource("Moo", BigInteger.valueOf(10));
    }

    public HashMapResourceHome<String, BigInteger> getCounterMap() {
        return counterMap;
    }

    public void setCounterMap(HashMapResourceHome<String, BigInteger> counterMap) {
        this.counterMap = counterMap;
    }

    @GetResourceProperty(namespace = "http://counter.com/CounterService", localPart = "Count")
    public BigInteger getCount(@Resourceful String counterId) {
        return counterMap.findResource(counterId);
    }

    @TerminateResource(immediate = true)
    public void destroyResource(@Resourceful String counterId) {
        logger.info("Destroying Counter resource with id = {}", counterId);
        counterMap.deleteResource(counterId);
    }

    @CreateResource("http://counter.com/CounterService/create")    
    public JAXBElement<String> createResource(CreateCounterRequest request){
        String id = UUID.randomUUID().toString();
        this.counterMap.addResource(id, BigInteger.valueOf(0));
        return fac.createCounterId(id);
    }
}
