package de.maxhenkel.plane.item;

import de.maxhenkel.plane.Main;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;

public class ItemWrench extends Item {

    public ItemWrench() {
        super(new Properties().maxStackSize(1).group(ItemGroup.MISC).maxDamage(1024));
        setRegistryName(new ResourceLocation(Main.MODID, "wrench"));
    }
}
