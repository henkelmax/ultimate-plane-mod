package de.maxhenkel.plane.entity;

import de.maxhenkel.corelib.item.ItemUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public abstract class EntityPlaneInventoryBase extends EntityPlaneFuelBase {

    private IInventory inventory;

    public EntityPlaneInventoryBase(EntityType type, World worldIn) {
        super(type, worldIn);

        inventory = new Inventory(27);
    }

    @Override
    public ActionResultType interact(PlayerEntity player, Hand hand) {
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                openGUI(player, true);
            }
            return ActionResultType.SUCCESS;
        }

        return super.interact(player, hand);
    }

    public abstract void openGUI(PlayerEntity player, boolean outside);

    public IInventory getInventory() {
        return inventory;
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        ItemUtils.readInventory(compound, "Inventory", inventory);
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        ItemUtils.saveInventory(compound, "Inventory", inventory);
    }

}
