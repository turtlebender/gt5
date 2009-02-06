package org.globus.wsrf.properties.impl;

import org.globus.wsrf.Resourceful;
import org.globus.wsrf.properties.GetResourcePropertyProvider;
import org.globus.wsrf.properties.ResourcePropertySet;
import org.globus.wsrf.annotations.AddressingAction;
import org.oasis.wsrf.resourceproperties.GetResourcePropertyResponse;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

public class DefaultGetResourcePropertyProvider implements GetResourcePropertyProvider {
    private ResourcePropertySet propSet;

    public ResourcePropertySet getPropSet() {
        return propSet;
    }

    public void setPropSet(ResourcePropertySet propSet) {
        this.propSet = propSet;
    }

    @SuppressWarnings("unchecked")
    @AddressingAction("http://docs.open-oasis.org/wsrf/rpw-2/GetResourceProperty/GetResourcePropertyRequest")
    public GetResourcePropertyResponse invokeInternal(@Resourceful Object id, JAXBElement<QName> resourceProperty)
            throws Exception {
        GetResourcePropertyResponse response = new GetResourcePropertyResponse();
        QName propName = resourceProperty.getValue();
        Object value = propSet.getResourceProperty(id, propName);
        response.getAny().add(new JAXBElement(propName, value.getClass(), value));
        return response;
    }
}
