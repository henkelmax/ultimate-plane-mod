package de.maxhenkel.plane.integration.waila;

import de.maxhenkel.plane.entity.EntityPlane;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;
import mcp.mobius.waila.api.event.WailaRenderEvent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@WailaPlugin
public class PluginPlane implements IWailaPlugin {

    @Override
    public void register(IRegistrar registrar) {
        registrar.registerComponentProvider(HUDHandlerPlanes.INSTANCE, TooltipPosition.HEAD, EntityPlane.class);
        registrar.registerComponentProvider(HUDHandlerPlanes.INSTANCE, TooltipPosition.BODY, EntityPlane.class);
        registrar.registerComponentProvider(HUDHandlerPlanes.INSTANCE, TooltipPosition.TAIL, EntityPlane.class);
    }

    @SubscribeEvent
    public void onWailaRender(WailaRenderEvent.Pre event) {
        if (!(event.getAccessor().getEntity() instanceof EntityPlane)) {
            return;
        }

        EntityPlane plane = (EntityPlane) event.getAccessor().getEntity();

        if (plane.getPassengers().contains(Minecraft.getInstance().player)) {
            event.setCanceled(true);
        }
    }

}
