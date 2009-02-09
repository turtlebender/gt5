package org.globus.wsrf.lifetime;

import org.globus.wsrf.annotations.AddressingAction;
import org.oasis.wsrf.lifetime.SetTerminationTime;
import org.oasis.wsrf.lifetime.SetTerminationTimeResponse;


public interface SetTerminationTimeProvider {

    @AddressingAction("http://docs.oasis-open.org/wsrf/rlw-2/ScheduledResourceTermination/SetTerminationTimeRequest")
    public SetTerminationTimeResponse setTerminationTime(SetTerminationTime time) throws Exception;
}
