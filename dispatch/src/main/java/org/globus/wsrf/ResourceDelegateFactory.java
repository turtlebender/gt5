package org.globus.wsrf;

/**
 * Created by IntelliJ IDEA.
 * User: turtlebender
 * Date: Feb 5, 2009
 * Time: 5:31:26 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ResourceDelegateFactory<T> {
    boolean supports(Object o);

    T getDelegate(Object o);

    Class<T> getInterface();

}
