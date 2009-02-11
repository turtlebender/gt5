package org.globus.wsrf.properties.impl;

import org.globus.wsrf.annotations.AddressingAction;
import org.globus.wsrf.properties.SetResourcePropertyProvider;
import org.oasis.wsrf.resourceproperties.SetResourceProperties;
import org.oasis.wsrf.resourceproperties.SetResourcePropertiesResponse;
import org.oasis.wsrf.resourceproperties.InsertResourceProperties;

import java.util.List;

//TODO: This isn't going to get done right away.
public class AnnotatedSetResourcePropertyProvider implements SetResourcePropertyProvider{

    @AddressingAction("http://docs.oasis-open.org/wsrf/rpw-2/SetResourceProperties/SetResourcePropertiesRequest")
    public SetResourcePropertiesResponse setResourceProperties(SetResourceProperties props) throws Exception {
        List<Object> operations = props.getInsertOrUpdateOrDelete();
        
        for(Object operation: operations){
            if(operation instanceof InsertResourceProperties){
                InsertResourceProperties insert = (InsertResourceProperties) operation;
                for(Object target : insert.getInsert().getAny()){
                    
                }
            }
        }
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
