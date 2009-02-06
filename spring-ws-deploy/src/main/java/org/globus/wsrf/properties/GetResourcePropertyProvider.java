package org.globus.wsrf.properties;

import org.globus.wsrf.annotations.AddressingAction;
import org.globus.wsrf.Resourceful;
import org.oasis.wsrf.resourceproperties.GetResourcePropertyResponse;

import javax.xml.namespace.QName;
import javax.xml.bind.JAXBElement;

/**
 * Created by IntelliJ IDEA.
 * User: turtlebender
 * Date: Feb 5, 2009
 * Time: 5:13:58 PM
 * To change this template use File | Settings | File Templates.
 */
public interface GetResourcePropertyProvider {
    @AddressingAction("http://docs.open-oasis.org/wsrf/rpw-2/GetResourceProperty/GetResourcePropertyRequest")
    GetResourcePropertyResponse invokeInternal(@Resourceful Object id, JAXBElement<QName> resourceProperty)
            throws Exception;
}
