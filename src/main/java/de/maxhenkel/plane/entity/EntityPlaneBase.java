package de.maxhenkel.plane.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public abstract class EntityPlaneBase extends EntityVehicleBase {

    public EntityPlaneBase(EntityType type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    public Vector3d getDismountLocationForPassenger(LivingEntity passenger) {
        return getPlayerOffsets()[0].add(new Vector3d(getX(), getY() + 0.1D, getZ()));
    }

    @Override
    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {

    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT compound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT compound) {

    }

    public void damagePlane(double damage, boolean horizontal) {

    }

    public abstract float getPlayerScaleFactor();

    @Override
    public int getPassengerSize() {
        return getPlayerOffsets().length;
    }

}
