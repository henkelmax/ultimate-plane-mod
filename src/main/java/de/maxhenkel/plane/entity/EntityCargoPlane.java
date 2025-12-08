package de.maxhenkel.plane.entity;

import de.maxhenkel.corelib.item.ItemUtils;
import de.maxhenkel.plane.PlaneMod;
import de.maxhenkel.plane.gui.ContainerPlane;
import de.maxhenkel.plane.net.MessagePlaneGui;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import javax.annotation.Nullable;

public class EntityCargoPlane extends EntityPlaneBase {

    private static final Vec3 BODY_CENTER = new Vec3(0D, 0D, -17.5D / 16D);

    private final Container cargoInventory;

    public EntityCargoPlane(Level world) {
        this(PlaneMod.CARGO_PLANE_ENTITY_TYPE.get(), world);
    }

    public EntityCargoPlane(EntityType<?> type, Level world) {
        super(type, world);
        cargoInventory = new SimpleContainer(54);
    }

    @Override
    public float getPlayerScaleFactor() {
        return 0.8F;
    }

    @Override
    public void destroyPlane(ServerLevel level, DamageSource source, Player player) {
        Containers.dropContents(level(), blockPosition(), cargoInventory);
        cargoInventory.clearContent();
        super.destroyPlane(level, source, player);
    }

    @Override
    public void openGUI(Player player, boolean outside) {
        if (player instanceof ServerPlayer serverPlayer) {
            if (outside) {
                serverPlayer.openMenu(new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return Component.translatable("gui.plane.cargo_inventory");
                    }

                    @Nullable
                    @Override
                    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
                        return ChestMenu.sixRows(i, playerInventory, cargoInventory);
                    }
                });
            } else {
                serverPlayer.openMenu(new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return getName();
                    }

                    @Nullable
                    @Override
                    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
                        return new ContainerPlane(i, EntityCargoPlane.this, playerInventory);
                    }
                }, packetBuffer -> {
                    packetBuffer.writeUUID(getUUID());
                });
            }
        } else {
            ClientPacketDistributor.sendToServer(new MessagePlaneGui(player, outside));
        }
    }

    @Override
    public void readAdditionalSaveData(ValueInput valueInput) {
        super.readAdditionalSaveData(valueInput);
        ItemUtils.readInventory(valueInput, "CargoInventory", cargoInventory);
    }

    @Override
    public Vec3 getBodyRotationCenter() {
        return BODY_CENTER;
    }

    @Override
    public void addAdditionalSaveData(ValueOutput valueOutput) {
        super.addAdditionalSaveData(valueOutput);
        ItemUtils.saveInventory(valueOutput, "CargoInventory", cargoInventory);
    }

    @Override
    public int getFuelCapacity() {
        return PlaneMod.SERVER_CONFIG.cargoPlaneFuelCapacity.get();
    }

    @Override
    protected float getBaseFuelUsage() {
        return PlaneMod.SERVER_CONFIG.cargoPlaneBaseFuelUsage.get().floatValue();
    }

    @Override
    public double getFallSpeed() {
        return 0.11D;
    }

    @Override
    public Vec3[] getPlayerOffsets() {
        return new Vec3[]{new Vec3(0.5D, 0D, 0.8D), new Vec3(-0.5D, 0D, 0.8D)};
    }

    @Override
    public ResourceKey<LootTable> getPlaneLootTable() {
        return ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(PlaneMod.MODID, "entities/cargo_plane_" + getPlaneType().getTypeName()));
    }

}