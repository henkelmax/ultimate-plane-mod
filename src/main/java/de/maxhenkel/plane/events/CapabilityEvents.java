package de.maxhenkel.plane.events;

import de.maxhenkel.plane.Main;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CapabilityEvents {

    @SubscribeEvent
    public void capabilityAttach(AttachCapabilitiesEvent<Entity> event) {
        if (!((Entity) event.getObject()).getType().getRegistryName().getNamespace().equals(Main.MODID)) {
            return;
        }
        if (event.getObject() instanceof IFluidHandler) {
            IFluidHandler handler = (IFluidHandler) event.getObject();
            event.addCapability(new ResourceLocation(Main.MODID, "fluid"), new ICapabilityProvider() {
                @Nonnull
                @Override
                public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
                    if (cap.equals(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)) {
                        return LazyOptional.of(() -> (T) handler);
                    }
                    return LazyOptional.empty();
                }
            });
        }
    }

}
