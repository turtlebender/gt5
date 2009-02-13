package org.globus.wsrf.properties;

import org.globus.wsrf.annotations.AddressingAction;
import org.globus.wsrf.Resourceful;
import org.oasis.wsrf.resourceproperties.GetResourcePropertyResponse;

import javax.xml.namespace.QName;
import javax.xml.bind.JAXBElement;


public interface GetResourcePropertyProvider {
    @AddressingAction("http://docs.open-oasis.org/wsrf/rpw-2/GetResourceProperty/GetResourcePropertyRequest")
    GetResourcePropertyResponse getResourceProperty(@Resourceful Object id, JAXBElement<QName> resourceProperty)
            throws Exception;
}
