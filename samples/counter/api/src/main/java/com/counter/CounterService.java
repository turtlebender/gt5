package com.counter;

import com.counter.counterservice.IncrementRequest;
import org.globus.wsrf.Resourceful;
import org.globus.wsrf.annotations.AddressingAction;

import javax.xml.bind.JAXBElement;
import java.math.BigInteger;

/**
 * Created by IntelliJ IDEA.
 * User: turtlebender
 * Date: Feb 12, 2009
 * Time: 3:48:41 PM
 * To change this template use File | Settings | File Templates.
 */
public interface CounterService {
    @AddressingAction(namespace="http://counter.com/", path="CounterService/increment")
    JAXBElement<BigInteger> increment(@Resourceful String counterId, IncrementRequest request);
}
