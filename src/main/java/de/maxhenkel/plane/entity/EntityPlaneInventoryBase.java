package de.maxhenkel.plane.entity;

import de.maxhenkel.corelib.item.ItemUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public abstract class EntityPlaneInventoryBase extends EntityPlaneFuelBase {

    private Container inventory;

    public EntityPlaneInventoryBase(EntityType type, Level worldIn) {
        super(type, worldIn);

        inventory = new SimpleContainer(27);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (player.isShiftKeyDown()) {
            if (!level().isClientSide) {
                openGUI(player, true);
            }
            return InteractionResult.SUCCESS;
        }

        return super.interact(player, hand);
    }

    public abstract void openGUI(Player player, boolean outside);

    public Container getInventory() {
        return inventory;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        ItemUtils.readInventory(compound, "Inventory", inventory);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        ItemUtils.saveInventory(compound, "Inventory", inventory);
    }

}
