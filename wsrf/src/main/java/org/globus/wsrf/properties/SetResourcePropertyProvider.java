package org.globus.wsrf.properties;

import org.globus.wsrf.annotations.AddressingAction;
import org.oasis.wsrf.resourceproperties.SetResourceProperties;
import org.oasis.wsrf.resourceproperties.SetResourcePropertiesResponse;


public interface SetResourcePropertyProvider {
    @AddressingAction(namespace="http://docs.oasis-open.org/wsrf/rpw-2/SetResourceProperties/",
            path="SetResourcePropertiesRequest")
    public SetResourcePropertiesResponse setResourceProperties(SetResourceProperties props) throws Exception;
}
