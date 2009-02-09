package org.globus.wsrf.lifetime.impl;

import org.globus.wsrf.lifetime.FutureDestroyable;
import org.globus.wsrf.lifetime.SetTerminationTimeProvider;
import org.oasis.wsrf.lifetime.SetTerminationTime;
import org.oasis.wsrf.lifetime.SetTerminationTimeResponse;

import java.util.Calendar;
import java.util.GregorianCalendar;


public class DefaultSetTerminationTimeProvider implements SetTerminationTimeProvider{
    private FutureDestroyable destroyable;


    public DefaultSetTerminationTimeProvider() {
    }

    public DefaultSetTerminationTimeProvider(FutureDestroyable destroyable) {
        this.destroyable = destroyable;
    }

    public FutureDestroyable getDestroyable() {
        return destroyable;
    }

    public void setDestroyable(FutureDestroyable destroyable) {
        this.destroyable = destroyable;
    }

    public SetTerminationTimeResponse setTerminationTime(SetTerminationTime time) throws Exception {
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTimeInMillis(time.getRequestedLifetimeDuration().getTimeInMillis(Calendar.getInstance()));
        destroyable.setTerminationTime(cal);
        return new SetTerminationTimeResponse();
    }
}
