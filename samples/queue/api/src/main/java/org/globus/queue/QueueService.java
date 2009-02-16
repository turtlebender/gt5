package org.globus.queue;

import org.globus.queueservice.PopRequest;
import org.globus.queueservice.PopResponse;
import org.globus.queueservice.PushRequest;
import org.globus.queueservice.PushResponse;
import org.globus.wsrf.Resourceful;
import org.globus.wsrf.annotations.AddressingAction;
import org.globus.wsrf.annotations.CreateResource;
import org.globus.wsrf.annotations.StatefulResource;
import org.globus.wsrf.annotations.GetResourceProperty;

import javax.xml.bind.JAXBElement;
import java.math.BigInteger;

@StatefulResource(resourceLocalpart = "queue", resourceNamespace = "http://www.globus.org",
        keyNamespace = "http://www.globus.org", keyLocalpart = "queueKey")
public interface QueueService {


    @GetResourceProperty(namespace = "http://www.globus/queue", localPart = "size")
    JAXBElement<BigInteger> getQueueSize(@Resourceful String queueId);

    @AddressingAction("http://www.globus.org/queue/push")
    @SuppressWarnings("unchecked")
    PushResponse push(@Resourceful String queueId, PushRequest request);

    @AddressingAction("http://www.globus.org/queue/pop")
    PopResponse pop(@Resourceful String queueId, PopRequest request);

    @CreateResource("http://www.globus.org/queue/create")
    JAXBElement<String> createQueue(JAXBElement<String> queueId);
}
