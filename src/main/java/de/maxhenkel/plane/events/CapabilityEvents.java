package de.maxhenkel.plane.events;

import de.maxhenkel.plane.Main;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.capabilities.Capabilities;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.util.LazyOptional;
import net.neoforged.neoforge.event.AttachCapabilitiesEvent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CapabilityEvents {

    @SubscribeEvent
    public void capabilityAttach(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() == null) {
            return;
        }
        if (BuiltInRegistries.ENTITY_TYPE.containsValue(event.getObject().getType())) {
            return;
        }
        ResourceLocation key = BuiltInRegistries.ENTITY_TYPE.getKey(event.getObject().getType());
        if (!key.getNamespace().equals(Main.MODID)) {
            return;
        }
        if (event.getObject() instanceof IFluidHandler) {
            IFluidHandler handler = (IFluidHandler) event.getObject();
            event.addCapability(new ResourceLocation(Main.MODID, "fluid"), new ICapabilityProvider() {
                @Nonnull
                @Override
                public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
                    if (cap.equals(Capabilities.FLUID_HANDLER)) {
                        return LazyOptional.of(() -> (T) handler);
                    }
                    return LazyOptional.empty();
                }
            });
        }
    }

}
