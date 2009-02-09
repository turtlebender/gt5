package com.counter;

import org.globus.wsrf.Resourceful;
import org.globus.wsrf.annotations.GetResourceProperty;
import org.globus.wsrf.annotations.StatefulResource;
import org.globus.wsrf.annotations.TerminateResource;
import org.springframework.beans.factory.InitializingBean;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@StatefulResource(keyNamespace = "http://counter.com/CounterService", keyLocalpart = "CounterId",
        resourceNamespace = "http://counter.com/CounterService", resourceLocalpart = "Counter")
public class CounterResource implements InitializingBean {
    private Map<String, BigInteger> counterMap;

    public void afterPropertiesSet() throws Exception {
        if (counterMap == null) {
            counterMap = new HashMap<String, BigInteger>();
        }
        counterMap.put("Moo", BigInteger.valueOf(10));
    }

    public Map<String, BigInteger> getCounterMap() {
        return counterMap;
    }

    public void setCounterMap(Map<String, BigInteger> counterMap) {
        this.counterMap = counterMap;
    }

    @GetResourceProperty(namespace = "http://counter.com/CounterService", localPart = "Count")
    public BigInteger getCount(@Resourceful String counterId) {
        return counterMap.get(counterId);
    }

    @TerminateResource(immediate = true)
    public void destroyResource(@Resourceful String counterId) {
        counterMap.remove(counterId);
    }
}
