package de.maxhenkel.plane.events;

import de.maxhenkel.plane.item.ItemBushPlane;
import de.maxhenkel.plane.item.ItemCargoPlane;
import de.maxhenkel.plane.item.ItemPlane;
import de.maxhenkel.plane.item.ModItems;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryObject;

public class CreativeTabEvents {

    @SubscribeEvent
    public static void onCreativeModeTabBuildContents(CreativeModeTabEvent.BuildContents event) {
        if (event.getTab() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.register((flags, builder, hasPermissions) -> {
                for (RegistryObject<ItemPlane> plane : ModItems.PLANES) {
                    builder.accept(new ItemStack(plane.get()));
                }
                for (RegistryObject<ItemBushPlane> plane : ModItems.BUSH_PLANES) {
                    builder.accept(new ItemStack(plane.get()));
                }
                for (RegistryObject<ItemCargoPlane> plane : ModItems.CARGO_PLANES) {
                    builder.accept(new ItemStack(plane.get()));
                }
            });
        }
        if (event.getTab() == CreativeModeTabs.INGREDIENTS) {
            event.register((flags, builder, hasPermissions) -> {
                builder.accept(new ItemStack(ModItems.PLANE_ENGINE.get()));
                builder.accept(new ItemStack(ModItems.PLANE_WHEEL.get()));
                builder.accept(new ItemStack(ModItems.WRENCH.get()));
                builder.accept(new ItemStack(ModItems.DIAMOND_REINFORCED_IRON.get()));
            });
        }
    }

}
