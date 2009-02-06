package org.globus.wsrf.test;

import org.globus.wsrf.properties.Resource;

import javax.xml.namespace.QName;

public class DefaultResource implements Resource {
    private QName resourceKeyName;
    private String keyNamespace;
    private String keyLocalpart;

    public void setKeyNamespace(String keyNamespace){
        this.keyNamespace = keyNamespace;
    }

    public void setKeyLocalPart(String localPart){
        this.keyLocalpart = localPart;
    }

    public void init(){
        resourceKeyName = new QName(keyNamespace, keyLocalpart);
    }  

    public QName getResourceKeyName() {
        return this.resourceKeyName;
    }
}
