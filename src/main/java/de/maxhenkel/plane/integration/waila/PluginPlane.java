package de.maxhenkel.plane.integration.waila;

import de.maxhenkel.plane.entity.EntityPlaneBase;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class PluginPlane implements IWailaPlugin {

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerEntityComponent(HUDHandlerPlanes.INSTANCE, EntityPlaneBase.class);
    }

}
