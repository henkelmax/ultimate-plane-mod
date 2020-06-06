package de.maxhenkel.plane;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

public class ItemTools {

    public static void saveInventory(CompoundNBT compound, String name, IInventory inv) {
        ListNBT nbttaglist = new ListNBT();

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            if (!inv.getStackInSlot(i).isEmpty()) {
                CompoundNBT tag = new CompoundNBT();
                tag.putInt("Slot", i);
                inv.getStackInSlot(i).write(tag);
                nbttaglist.add(tag);
            }
        }

        compound.put(name, nbttaglist);
    }

    public static void readInventory(CompoundNBT compound, String name, IInventory inv) {
        if (!compound.contains(name)) {
            return;
        }

        ListNBT list = compound.getList(name, 10);
        for (int i = 0; i < list.size(); i++) {
            CompoundNBT tag = list.getCompound(i);
            int j = tag.getInt("Slot");

            if (j >= 0 && j < inv.getSizeInventory()) {
                inv.setInventorySlotContents(j, ItemStack.read(tag));
            }
        }
    }

}
