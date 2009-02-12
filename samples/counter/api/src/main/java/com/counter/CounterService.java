package com.counter;

import org.globus.wsrf.annotations.AddressingAction;
import org.globus.wsrf.Resourceful;

import javax.xml.bind.JAXBElement;
import java.math.BigInteger;

import com.counter.counterservice.IncrementRequest;

/**
 * Created by IntelliJ IDEA.
 * User: turtlebender
 * Date: Feb 12, 2009
 * Time: 3:48:41 PM
 * To change this template use File | Settings | File Templates.
 */
public interface CounterService {
    @AddressingAction("http://counter.com/CounterService/increment")
    JAXBElement<BigInteger> increment(@Resourceful String counterId, IncrementRequest request);
}
