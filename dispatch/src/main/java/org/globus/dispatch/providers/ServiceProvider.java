package org.globus.dispatch.providers;

import org.globus.dispatch.exception.DispatchException;

import javax.xml.transform.Source;

/**
 * Created by IntelliJ IDEA.
 * User: turtlebender
 * Date: Jan 12, 2009
 * Time: 2:05:44 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ServiceProvider<X extends Source> {

    public X invoke(X source) throws DispatchException;
    
}
