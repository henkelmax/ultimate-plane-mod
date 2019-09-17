package de.maxhenkel.plane.integration.waila;

import de.maxhenkel.plane.entity.EntityPlane;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;

@WailaPlugin
public class PluginPlane implements IWailaPlugin {

    @Override
    public void register(IRegistrar registrar) {
        registrar.registerComponentProvider(HUDHandlerPlanes.INSTANCE, TooltipPosition.HEAD, EntityPlane.class);
        registrar.registerComponentProvider(HUDHandlerPlanes.INSTANCE, TooltipPosition.BODY, EntityPlane.class);
        registrar.registerComponentProvider(HUDHandlerPlanes.INSTANCE, TooltipPosition.TAIL, EntityPlane.class);
    }

}
