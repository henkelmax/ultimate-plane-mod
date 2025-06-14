package de.maxhenkel.plane.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

public abstract class EntityFlyableBase extends EntityVehicleBase {

    public EntityFlyableBase(EntityType type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
        return getPlayerOffsets()[0].add(new Vec3(getX(), getY() + 0.1D, getZ()));
    }

    @Override
    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {

    }

    @Override
    public boolean causeFallDamage(double distance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    protected void readAdditionalSaveData(ValueInput valueInput) {

    }

    @Override
    protected void addAdditionalSaveData(ValueOutput valueOutput) {

    }

    public void damagePlane(float damage, boolean horizontal) {

    }

    public abstract float getPlayerScaleFactor();

    @Override
    public int getPassengerSize() {
        return getPlayerOffsets().length;
    }

}
