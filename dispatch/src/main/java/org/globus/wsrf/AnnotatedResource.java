package org.globus.wsrf;

import org.globus.wsrf.annotations.StatefulResource;
import org.globus.wsrf.properties.Resource;

import javax.xml.namespace.QName;

/**
 * Created by IntelliJ IDEA.
 * User: turtlebender
 * Date: Feb 6, 2009
 * Time: 9:23:40 AM
 * To change this template use File | Settings | File Templates.
 */
public class AnnotatedResource implements Resource {
    QName keyName;

    public AnnotatedResource(Object resource) {
        setResource(resource);
    }

    public AnnotatedResource() {
    }

    public void setResource(Object resource){
        StatefulResource sr = resource.getClass().getAnnotation(StatefulResource.class);
        if(sr == null){
            throw new IllegalArgumentException("Resource class must be annotated as StatefulResource");
        }
        keyName = new QName(sr.keyNamespace(), sr.keyLocalpart());
    }

    public QName getResourceKeyName() {
        return keyName;
    }
}
