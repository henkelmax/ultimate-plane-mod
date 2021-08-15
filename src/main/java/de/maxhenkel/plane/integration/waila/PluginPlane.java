package de.maxhenkel.plane.integration.waila;

import de.maxhenkel.plane.entity.EntityPlaneSoundBase;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;
import mcp.mobius.waila.api.event.WailaRenderEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@WailaPlugin
public class PluginPlane implements IWailaPlugin {

    @Override
    public void register(IRegistrar registrar) {
        registrar.registerComponentProvider(HUDHandlerPlanes.INSTANCE, TooltipPosition.HEAD, EntityPlaneSoundBase.class);
        registrar.registerComponentProvider(HUDHandlerPlanes.INSTANCE, TooltipPosition.BODY, EntityPlaneSoundBase.class);
        registrar.registerComponentProvider(HUDHandlerPlanes.INSTANCE, TooltipPosition.TAIL, EntityPlaneSoundBase.class);
    }

    @SubscribeEvent
    public void onWailaRender(WailaRenderEvent.Pre event) {
        if (event.getAccessor().getHitResult() instanceof EntityHitResult result) {
            if (result.getEntity() instanceof EntityPlaneSoundBase plane) {
                if (plane.getPassengers().contains(Minecraft.getInstance().player)) {
                    event.setCanceled(true);
                }
            }
        }
    }

}
