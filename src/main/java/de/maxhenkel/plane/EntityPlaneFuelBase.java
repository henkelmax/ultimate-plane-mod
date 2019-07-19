package de.maxhenkel.plane;

import de.maxhenkel.plane.entity.EntityPlaneControlBase;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

public class EntityPlaneFuelBase extends EntityPlaneControlBase {

    private static final DataParameter<Integer> FUEL = EntityDataManager.createKey(EntityPlaneControlBase.class,
            DataSerializers.VARINT);

    private static final float MAX_FUEL_USAGE = 5F;

    public EntityPlaneFuelBase(EntityType type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    public void tick() {
        super.tick();
        fuelTick();
    }

    public void fuelTick() {
        if (!isStarted()) {
            return;
        }

        if (world.getGameTime() % 20L == 0L) {
            int consumeAmount = Math.max((int) Math.ceil(getEngineSpeed() * MAX_FUEL_USAGE), 1);
            setFuel(Math.max(getFuel() - consumeAmount, 0));
            //System.out.println(getFuel());
        }
    }

    @Override
    public boolean canEngineBeStarted() {
        if (getFuel() <= 0) {
            return false;
        }
        return super.canEngineBeStarted();
    }

    @Override
    protected void registerData() {
        super.registerData();
        dataManager.register(FUEL, 0);
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        setFuel(compound.getInt("Fuel"));
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("Fuel", getFuel());
    }

    public int getFuel() {
        return dataManager.get(FUEL);
    }

    public void setFuel(int fuel) {
        dataManager.set(FUEL, fuel);
    }
}
