package de.maxhenkel.plane.item;

import de.maxhenkel.plane.Main;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ItemWrench extends Item {

    public ItemWrench() {
        super(new Properties().stacksTo(1).tab(ItemGroup.TAB_MISC).durability(1024));
        setRegistryName(new ResourceLocation(Main.MODID, "wrench"));
    }

    @Override
    public boolean isEnchantable(ItemStack p_77616_1_) {
        return false;
    }
}
