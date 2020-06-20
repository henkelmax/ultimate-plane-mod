package de.maxhenkel.plane.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class EntityPlaneBase extends EntityVehicleBase {


    public EntityPlaneBase(EntityType type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {

    }

    @Override
    public boolean onLivingFall(float distance, float damageMultiplier) {
        return false;
    }

    @Override
    protected void registerData() {

    }

    @Override
    protected void readAdditional(CompoundNBT compound) {

    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {

    }

    public void damagePlane(double damage, boolean horizontal) {

    }

    public int getPassengerSize() {
        return 3;
    }

    public Vec3d[] getPlayerOffsets() {
        return new Vec3d[]{new Vec3d(0D, 0D, 1D), new Vec3d(0D, 0D, 0.5D), new Vec3d(0D, 0D, 0D)};
    }
}
