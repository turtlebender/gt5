package org.globus.queue;

import org.globus.queueservice.PopRequest;
import org.globus.queueservice.PopResponse;
import org.globus.queueservice.PushRequest;
import org.globus.queueservice.PushResponse;
import org.globus.wsrf.Resourceful;
import org.globus.wsrf.annotations.AddressingAction;
import org.globus.wsrf.annotations.CreateResource;
import org.globus.wsrf.annotations.StatefulResource;

import javax.xml.bind.JAXBElement;

@StatefulResource(resourceLocalpart = "queue", resourceNamespace = "http://www.globus.org",
        keyNamespace = "http://www.globus.org", keyLocalpart = "queueKey")
public interface QueueService {

    @AddressingAction("http://www.globus.org/queue/push")
    public PushResponse push(@Resourceful String queueId, PushRequest request);

    @AddressingAction("http://www.globus.org/queue/pop")
    public PopResponse pop(@Resourceful String queueId, PopRequest request);

    @CreateResource("http://www.globus.org/queue/create")
    public JAXBElement<String> createQueue(JAXBElement<String> queueId);
}
