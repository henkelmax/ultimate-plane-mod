package de.maxhenkel.plane;

import de.maxhenkel.plane.item.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> TAB_REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, PlaneMod.MODID);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB_PLANES = TAB_REGISTER.register("planes", () -> {
        return CreativeModeTab.builder()
                .icon(() -> new ItemStack(ModItems.PLANES[0].get()))
                .displayItems((param, output) -> {
                    output.accept(new ItemStack(ModItems.PLANE_ENGINE.get()));
                    output.accept(new ItemStack(ModItems.PLANE_WHEEL.get()));
                    output.accept(new ItemStack(ModItems.WRENCH.get()));
                    output.accept(new ItemStack(ModItems.DIAMOND_REINFORCED_IRON.get()));
                    for (DeferredHolder<Item, ItemBushPlane> plane : ModItems.BUSH_PLANES) {
                        output.accept(new ItemStack(plane.get()));
                    }
                    for (DeferredHolder<Item, ItemPlane> plane : ModItems.PLANES) {
                        output.accept(new ItemStack(plane.get()));
                    }
                    for (DeferredHolder<Item, ItemCargoPlane> plane : ModItems.CARGO_PLANES) {
                        output.accept(new ItemStack(plane.get()));
                    }
                    for (DeferredHolder<Item, ItemTransporterPlane> plane : ModItems.TRANSPORTER_PLANES) {
                        output.accept(new ItemStack(plane.get()));
                    }
                })
                .title(Component.translatable("itemGroup.planes"))
                .build();
    });

    public static void init(IEventBus eventBus) {
        TAB_REGISTER.register(eventBus);
    }

}
