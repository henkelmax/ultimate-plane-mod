package de.maxhenkel.plane.item;

import de.maxhenkel.plane.Main;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

public class ItemCraftingComponent extends Item {

    public ItemCraftingComponent(String name) {
        super(new Properties().tab(CreativeModeTab.TAB_MISC));
        setRegistryName(new ResourceLocation(Main.MODID, name));
    }
}
