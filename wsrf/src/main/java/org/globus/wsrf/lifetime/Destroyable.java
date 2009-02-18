package org.globus.wsrf.lifetime;

import org.globus.wsrf.annotations.AddressingAction;
import org.globus.wsrf.Resourceful;
import org.oasis.wsrf.lifetime.DestroyResponse;


public interface Destroyable {
    @AddressingAction(namespace="http://docs.oasis-open.org/wsrf/rlw-2/ImmediateResourceTermination/",
            path="DestroyRequest")
    public DestroyResponse destroy(@Resourceful Object id) throws Exception;
}
