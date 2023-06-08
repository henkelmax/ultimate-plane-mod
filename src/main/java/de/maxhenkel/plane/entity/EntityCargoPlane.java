package de.maxhenkel.plane.entity;

import de.maxhenkel.corelib.item.ItemUtils;
import de.maxhenkel.plane.Main;
import de.maxhenkel.plane.gui.ContainerPlane;
import de.maxhenkel.plane.net.MessagePlaneGui;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public class EntityCargoPlane extends EntityPlaneSoundBase {

    private static final EntityDataAccessor<Integer> TYPE = SynchedEntityData.defineId(EntityCargoPlane.class, EntityDataSerializers.INT);
    private Container cargoInventory;

    public EntityCargoPlane(Level world) {
        this(Main.CARGO_PLANE_ENTITY_TYPE.get(), world);
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
    public void destroyPlane(DamageSource source, Player player) {
        Containers.dropContents(level(), blockPosition(), cargoInventory);
        cargoInventory.clearContent();
        super.destroyPlane(source, player);
    }

    @Override
    public void openGUI(Player player, boolean outside) {
        if (player instanceof ServerPlayer) {
            if (outside) {
                NetworkHooks.openScreen((ServerPlayer) player, new MenuProvider() {
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
                NetworkHooks.openScreen((ServerPlayer) player, new MenuProvider() {
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
            Main.SIMPLE_CHANNEL.sendToServer(new MessagePlaneGui(player, outside));
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        setPlaneType(EntityCargoPlane.Type.fromTypeName(compound.getString("Type")));
        ItemUtils.readInventory(compound, "CargoInventory", cargoInventory);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("Type", getPlaneType().getTypeName());
        ItemUtils.saveInventory(compound, "CargoInventory", cargoInventory);
    }

    @Override
    public float getMaxFuelUsage() {
        return 21F;
    }

    @Override
    public int getMaxFuel() {
        return 18000;
    }

    @Override
    public ResourceLocation getLootTable() {
        return new ResourceLocation(Main.MODID, "entities/cargo_plane_" + getPlaneType().getTypeName());
    }

    @Override
    public double getFallSpeed() {
        return 0.11D;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(TYPE, 0);
    }

    @Override
    public Vec3[] getPlayerOffsets() {
        return new Vec3[]{new Vec3(0.5D, 0D, 0.8D), new Vec3(-0.5D, 0D, 0.8D)};
    }

    public EntityCargoPlane.Type getPlaneType() {
        return EntityCargoPlane.Type.values()[entityData.get(TYPE)];
    }

    public void setPlaneType(EntityCargoPlane.Type type) {
        entityData.set(TYPE, type.ordinal());
    }

    public static enum Type {
        OAK("oak"),
        SPRUCE("spruce"),
        BIRCH("birch"),
        JUNGLE("jungle"),
        ACACIA("acacia"),
        DARK_OAK("dark_oak"),
        WARPED("warped"),
        CRIMSON("crimson");

        private String name;

        Type(String name) {
            this.name = name;
        }

        public String getTypeName() {
            return name;
        }

        public static EntityCargoPlane.Type fromTypeName(String name) {
            for (EntityCargoPlane.Type type : values()) {
                if (type.getTypeName().equals(name)) {
                    return type;
                }
            }
            return OAK;
        }
    }

}