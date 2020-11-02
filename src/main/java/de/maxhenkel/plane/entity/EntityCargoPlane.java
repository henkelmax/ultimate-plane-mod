package de.maxhenkel.plane.entity;

import de.maxhenkel.corelib.item.ItemUtils;
import de.maxhenkel.plane.Main;
import de.maxhenkel.plane.gui.ContainerPlane;
import de.maxhenkel.plane.net.MessagePlaneGui;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class EntityCargoPlane extends EntityPlaneSoundBase {

    private static final DataParameter<Integer> TYPE = EntityDataManager.createKey(EntityPlaneSoundBase.class, DataSerializers.VARINT);
    private IInventory cargoInventory;

    public EntityCargoPlane(World world) {
        this(Main.CARGO_PLANE_ENTITY_TYPE, world);
    }

    public EntityCargoPlane(EntityType<?> type, World world) {
        super(type, world);
        cargoInventory = new Inventory(54);
    }

    @Override
    public float getPlayerScaleFactor() {
        return 0.8F;
    }

    @Override
    public void destroyPlane(DamageSource source, PlayerEntity player) {
        InventoryHelper.dropInventoryItems(world, getPosition(), cargoInventory);
        cargoInventory.clear();
        super.destroyPlane(source, player);
    }

    @Override
    public void openGUI(PlayerEntity player, boolean outside) {
        if (player instanceof ServerPlayerEntity) {
            if (outside) {
                NetworkHooks.openGui((ServerPlayerEntity) player, new INamedContainerProvider() {
                    @Override
                    public ITextComponent getDisplayName() {
                        return new TranslationTextComponent("gui.plane.cargo_inventory");
                    }

                    @Nullable
                    @Override
                    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                        return ChestContainer.createGeneric9X6(i, playerInventory, cargoInventory);
                    }
                });
            } else {
                NetworkHooks.openGui((ServerPlayerEntity) player, new INamedContainerProvider() {
                    @Override
                    public ITextComponent getDisplayName() {
                        return getName();
                    }

                    @Nullable
                    @Override
                    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                        return new ContainerPlane(i, EntityCargoPlane.this, playerInventory);
                    }
                }, packetBuffer -> {
                    packetBuffer.writeUniqueId(getUniqueID());
                });
            }
        } else {
            Main.SIMPLE_CHANNEL.sendToServer(new MessagePlaneGui(player, outside));
        }
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        setPlaneType(EntityCargoPlane.Type.fromTypeName(compound.getString("Type")));
        ItemUtils.readInventory(compound, "CargoInventory", cargoInventory);
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putString("Type", getPlaneType().getTypeName());
        ItemUtils.saveInventory(compound, "CargoInventory", cargoInventory);
    }

    @Override
    public float getMaxFuelUsage() {
        return 7F;
    }

    @Override
    public int getMaxFuel() {
        return 6000;
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
    protected void registerData() {
        super.registerData();
        dataManager.register(TYPE, 0);
    }

    @Override
    public Vector3d[] getPlayerOffsets() {
        return new Vector3d[]{new Vector3d(0.5D, 0D, 0.8D), new Vector3d(-0.5D, 0D, 0.8D)};
    }

    public EntityCargoPlane.Type getPlaneType() {
        return EntityCargoPlane.Type.values()[dataManager.get(TYPE)];
    }

    public void setPlaneType(EntityCargoPlane.Type type) {
        dataManager.set(TYPE, type.ordinal());
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