package de.maxhenkel.plane.entity;

import de.maxhenkel.plane.PlaneType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public abstract class EntityPlaneBase extends EntityPlaneSoundBase {

    private static final EntityDataAccessor<Integer> TYPE = SynchedEntityData.defineId(EntityPlaneBase.class, EntityDataSerializers.INT);

    public EntityPlaneBase(EntityType type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TYPE, 0);
    }

    public PlaneType getPlaneType() {
        return PlaneType.values()[entityData.get(TYPE)];
    }

    public void setPlaneType(PlaneType type) {
        entityData.set(TYPE, type.ordinal());
    }

    @Nullable
    private Component typeName;

    @Override
    protected Component getTypeName() {
        if (typeName == null) {
            typeName = Component.translatable(getType().getDescriptionId() + "." + getPlaneType().getTypeName());
        }
        return typeName;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("Type", getPlaneType().getTypeName());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        setPlaneType(PlaneType.fromTypeName(compound.getString("Type")));
    }

}
