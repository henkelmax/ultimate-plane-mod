package de.maxhenkel.plane.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class EntityPlaneBase extends EntityVehicleBase {


    public EntityPlaneBase(EntityType type, World worldIn) {
        super(type, worldIn);
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
        return 1;
    }

    public Vec3d[] getPlayerOffsets() {
        return new Vec3d[]{new Vec3d(0D, 0D, 0D)};
    }
}
