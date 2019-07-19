package de.maxhenkel.plane.entity;

import de.maxhenkel.plane.ItemTools;
import de.maxhenkel.plane.Main;
import de.maxhenkel.plane.gui.ContainerPlane;
import de.maxhenkel.plane.net.MessagePlaneGui;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class EntityPlaneInventoryBase extends EntityPlaneFuelBase {

    private IInventory inventory;

    public EntityPlaneInventoryBase(EntityType type, World worldIn) {
        super(type, worldIn);

        inventory = new Inventory(27);
    }

    @Override
    public boolean processInitialInteract(PlayerEntity player, Hand hand) {

        if (player.isSneaking()) {
            if (!world.isRemote) {
                openGUI(player);
            }
            return true;
        }

        return super.processInitialInteract(player, hand);
    }

    public void openGUI(PlayerEntity player) {
        if (player instanceof ServerPlayerEntity) {
            NetworkHooks.openGui((ServerPlayerEntity) player, new INamedContainerProvider() {
                @Override
                public ITextComponent getDisplayName() {
                    return getName();
                }

                @Nullable
                @Override
                public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                    return new ContainerPlane(i, (EntityPlane) EntityPlaneInventoryBase.this, playerInventory);
                }
            }, packetBuffer -> {
                packetBuffer.writeUniqueId(getUniqueID());
            });
        } else {
            Main.SIMPLE_CHANNEL.sendToServer(new MessagePlaneGui(player));
        }
    }

    public IInventory getInventory() {
        return inventory;
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        ItemTools.readInventory(compound, "Inventory", inventory);
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        ItemTools.saveInventory(compound, "Inventory", inventory);
    }
}
