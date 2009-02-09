package org.globus.wsrf.lifetime.impl;

import org.globus.wsrf.Resourceful;
import org.globus.wsrf.annotations.AddressingAction;
import org.globus.wsrf.lifetime.DestroyProvider;
import org.globus.wsrf.lifetime.Destroyable;
import org.oasis.wsrf.lifetime.DestroyResponse;


public class DefaultDestroyProvider implements DestroyProvider {
    private Destroyable destroyable;

    public DefaultDestroyProvider() {
    }

    public DefaultDestroyProvider(Destroyable destroyable) {
        this.destroyable = destroyable;
    }

    public Destroyable getDestroyable() {
        return destroyable;
    }

    public void setDestroyable(Destroyable destroyable) {
        this.destroyable = destroyable;
    }

    @AddressingAction("http://docs.oasis-open.org/wsrf/rlw-2/ImmediateResourceTermination/DestroyRequest")
    public DestroyResponse destroy(@Resourceful Object id) throws Exception{
        destroyable.destroy(id);
        return new DestroyResponse();
    }
}
