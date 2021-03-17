package de.maxhenkel.plane.item;

import de.maxhenkel.plane.Main;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;

public class ItemCraftingComponent extends Item {

    public ItemCraftingComponent(String name) {
        super(new Properties().tab(ItemGroup.TAB_MISC));
        setRegistryName(new ResourceLocation(Main.MODID, name));
    }
}
