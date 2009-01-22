package org.globus.dispatch.stateful;

import javax.xml.namespace.QName;

/**
 * Created by IntelliJ IDEA.
 * User: turtlebender
 * Date: Jan 21, 2009
 * Time: 2:56:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class ServiceResourceMetaData {
    private QName resourceName;
    private QName resourceKeyName;

    public QName getResourceName() {
        return resourceName;
    }

    public void setResourceName(QName resourceName) {
        this.resourceName = resourceName;
    }

    public QName getResourceKeyName() {
        return resourceKeyName;
    }

    public void setResourceKeyName(QName resourceKeyName) {
        this.resourceKeyName = resourceKeyName;
    }
}
