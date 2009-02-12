package org.globus.queue;

import org.globus.queueservice.ObjectFactory;
import org.globus.queueservice.PopRequest;
import org.globus.queueservice.PopResponse;
import org.globus.queueservice.PushRequest;
import org.globus.queueservice.PushResponse;
import org.globus.wsrf.Resourceful;
import org.globus.wsrf.annotations.AddressingAction;
import org.globus.wsrf.annotations.StatefulResource;
import org.globus.wsrf.annotations.CreateResource;
import org.globus.wsrf.annotations.GetResourceProperty;

import javax.xml.bind.JAXBElement;
import java.math.BigInteger;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@StatefulResource(resourceLocalpart = "queue", resourceNamespace = "http://www.globus.org",
        keyNamespace = "http://www.globus.org", keyLocalpart = "queueKey")
public class DefaultQueueService<T> implements QueueService {
    Map<String, Queue<T>> queueMap = new ConcurrentHashMap<String, Queue<T>>();
    private ObjectFactory fac = new ObjectFactory();

    @GetResourceProperty(namespace = "http://www.globus/queue", localPart = "size")
    public JAXBElement<BigInteger> getQueueSize(@Resourceful String queueId) {
        Queue queue = queueMap.get(queueId);
        return fac.createQueueSize(BigInteger.valueOf(queue.size()));
    }

    @AddressingAction("http://www.globus.org/queue/push")
    @SuppressWarnings("unchecked")
    public PushResponse push(@Resourceful String queueId, PushRequest request) {
        Queue<T> queue = queueMap.get(queueId);
        queue.add((T) request.getQueueItem());
        return fac.createPushResponse();
    }

    @AddressingAction("http://www.globus.org/queue/pop")
    public PopResponse pop(@Resourceful String queueId, PopRequest request) {
        Queue<T> queue = queueMap.get(queueId);
        Object o = queue.remove();
        PopResponse response = fac.createPopResponse();
        response.setQueueItem(o);
        return response; 
    }

    @CreateResource("http://www.globus.org/queue/create")
    public JAXBElement<String> createQueue(JAXBElement<String> queueId) {
        queueMap.put(queueId.getValue(), new ConcurrentLinkedQueue<T>());
        return fac.createCreateQueueResponse(queueId.getValue());
    }
}
