package org.globus.wsrf.properties.impl;

import org.globus.wsrf.annotations.AddressingAction;
import org.globus.wsrf.Resourceful;
import org.globus.wsrf.properties.GetResourcePropertyProvider;
import org.oasis.wsrf.resourceproperties.GetMultipleResourceProperties;
import org.oasis.wsrf.resourceproperties.GetMultipleResourcePropertiesResponse;

import javax.xml.namespace.QName;
import java.util.List;

public class GetMultipleResourcePropertyProvider {
    private GetResourcePropertyProvider propSet;

    public GetResourcePropertyProvider getPropSet() {
        return propSet;
    }

    public void setPropSet(GetResourcePropertyProvider propSet) {
        this.propSet = propSet;
    }

    @AddressingAction("http://docs.open-oasis.org/wsrf/rpw-2/GetMultipleResourceProperties/GetMultipleResourcePropertiesRequest")
    public GetMultipleResourcePropertiesResponse invokeInternal(@Resourceful Object resource,
                                                                GetMultipleResourceProperties properties) throws Exception {
        GetMultipleResourcePropertiesResponse response = new GetMultipleResourcePropertiesResponse();
        List<Object> values = response.getAny();
        for(QName prop: properties.getResourceProperty()){
//            Object value = propSet.getResourceProperty(prop, resource);
//            values.add(new JAXBElement(prop, value.getClass(), value));
        }        
        return response;
    }
}
