package de.maxhenkel.plane.events;

import de.maxhenkel.plane.item.ItemBushPlane;
import de.maxhenkel.plane.item.ItemCargoPlane;
import de.maxhenkel.plane.item.ItemPlane;
import de.maxhenkel.plane.item.ModItems;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryObject;

public class CreativeTabEvents {

    @SubscribeEvent
    public static void onCreativeModeTabBuildContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(CreativeModeTabs.TOOLS_AND_UTILITIES)) {
            for (RegistryObject<ItemPlane> plane : ModItems.PLANES) {
                event.accept(new ItemStack(plane.get()));
            }
            for (RegistryObject<ItemBushPlane> plane : ModItems.BUSH_PLANES) {
                event.accept(new ItemStack(plane.get()));
            }
            for (RegistryObject<ItemCargoPlane> plane : ModItems.CARGO_PLANES) {
                event.accept(new ItemStack(plane.get()));
            }
        }
        if (event.getTabKey().equals(CreativeModeTabs.INGREDIENTS)) {
            event.accept(new ItemStack(ModItems.PLANE_ENGINE.get()));
            event.accept(new ItemStack(ModItems.PLANE_WHEEL.get()));
            event.accept(new ItemStack(ModItems.WRENCH.get()));
            event.accept(new ItemStack(ModItems.DIAMOND_REINFORCED_IRON.get()));
        }
    }

}
