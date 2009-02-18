package org.globus.wsrf.properties;

import org.globus.wsrf.ResourceException;
import org.globus.wsrf.Resourceful;
import org.globus.wsrf.annotations.AddressingAction;
import org.oasis.wsrf.resourceproperties.GetResourcePropertyResponse;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;


public interface GetResourcePropertyProvider {
    @AddressingAction(namespace="http://docs.open-oasis.org/wsrf/rpw-2/GetResourceProperty/",
            path="GetResourcePropertyRequest")
    GetResourcePropertyResponse getResourceProperty(@Resourceful Object id, JAXBElement<QName> resourceProperty)
            throws ResourceException;
}
