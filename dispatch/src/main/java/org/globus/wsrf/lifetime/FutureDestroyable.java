package org.globus.wsrf.lifetime;

import java.util.Calendar;

/**
 * Created by IntelliJ IDEA.
 * User: turtlebender
 * Date: Feb 6, 2009
 * Time: 8:45:32 PM
 * To change this template use File | Settings | File Templates.
 */
public interface FutureDestroyable {
    public void setTerminationTime(Calendar cal);
}
