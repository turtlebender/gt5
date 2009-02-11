package org.globus.wsrf.lifetime;

import org.globus.wsrf.annotations.AddressingAction;
import org.globus.wsrf.Resourceful;
import org.oasis.wsrf.lifetime.SetTerminationTime;
import org.oasis.wsrf.lifetime.SetTerminationTimeResponse;


public interface FutureDestroyable {
    @AddressingAction("http://docs.oasis-open.org/wsrf/rlw-2/ScheduledResourceTermination/SetTerminationTimeRequest")
    public SetTerminationTimeResponse setTerminationTime(@Resourceful Object resourceKey, SetTerminationTime time) throws Exception;
}
