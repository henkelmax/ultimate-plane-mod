package de.maxhenkel.plane.entity;

import de.maxhenkel.plane.Main;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class EntityBushPlane extends EntityPlaneSoundBase {

    private static final EntityDataAccessor<Integer> TYPE = SynchedEntityData.defineId(EntityBushPlane.class, EntityDataSerializers.INT);

    public EntityBushPlane(Level world) {
        this(Main.BUSH_PLANE_ENTITY_TYPE.get(), world);
    }

    public EntityBushPlane(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("Type", getPlaneType().getTypeName());
    }

    @Override
    public float getPlayerScaleFactor() {
        return 0.8F;
    }

    @Override
    protected boolean isStalling(Vec3 motionVector) {
        return motionVector.multiply(1D, 0D, 1D).length() < -motionVector.y;
    }

    @Override
    public void openGUI(Player player, boolean outside) {

    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
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
    public Vec3[] getPlayerOffsets() {
        return new Vec3[]{new Vec3(0D, 0D, 0.5D)};
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
