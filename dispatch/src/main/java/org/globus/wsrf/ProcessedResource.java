package org.globus.wsrf;

import org.globus.wsrf.properties.Resource;

import javax.xml.namespace.QName;

public class ProcessedResource {
    private Resource resource;
    private QName qname;

    public ProcessedResource() {
    }

    public ProcessedResource(Resource resource, QName qname) {
        this.resource = resource;
        this.qname = qname;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public QName getQname() {
        return qname;
    }

    public void setQname(QName qname) {
        this.qname = qname;
    }
}
