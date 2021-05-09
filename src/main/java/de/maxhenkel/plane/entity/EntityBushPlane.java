package de.maxhenkel.plane.entity;

import de.maxhenkel.plane.Main;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class EntityBushPlane extends EntityPlaneSoundBase {

    private static final DataParameter<Integer> TYPE = EntityDataManager.defineId(EntityBushPlane.class, DataSerializers.INT);

    public EntityBushPlane(World world) {
        this(Main.BUSH_PLANE_ENTITY_TYPE, world);
    }

    public EntityBushPlane(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("Type", getPlaneType().getTypeName());
    }

    @Override
    public float getPlayerScaleFactor() {
        return 0.8F;
    }

    @Override
    protected boolean isStalling(Vector3d motionVector) {
        return motionVector.multiply(1D, 0D, 1D).length() < -motionVector.y;
    }

    @Override
    public void openGUI(PlayerEntity player, boolean outside) {

    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        setPlaneType(Type.fromTypeName(compound.getString("Type")));
    }

    @Override
    public ResourceLocation getLootTable() {
        return new ResourceLocation(Main.MODID, "entities/bush_plane_" + getPlaneType().getTypeName());
    }

    @Override
    public float getMaxFuelUsage() {
        return 15F;
    }

    @Override
    public int getMaxFuel() {
        return 6000;
    }

    @Override
    public double getFallSpeed() {
        return 0.08D;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(TYPE, 0);
    }

    @Override
    public Vector3d[] getPlayerOffsets() {
        return new Vector3d[]{new Vector3d(0D, 0D, 0.5D)};
    }

    public Type getPlaneType() {
        return Type.values()[entityData.get(TYPE)];
    }

    public void setPlaneType(Type type) {
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

        public static Type fromTypeName(String name) {
            for (Type type : values()) {
                if (type.getTypeName().equals(name)) {
                    return type;
                }
            }
            return OAK;
        }
    }

}
