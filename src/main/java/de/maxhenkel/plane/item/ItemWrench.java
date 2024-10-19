package de.maxhenkel.plane.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemWrench extends Item {

    public ItemWrench(Properties properties) {
        super(properties.stacksTo(1).durability(1024));
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false;
    }

}
