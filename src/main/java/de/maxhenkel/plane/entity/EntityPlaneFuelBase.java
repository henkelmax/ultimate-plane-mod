package de.maxhenkel.plane.entity;

import de.maxhenkel.plane.Main;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class EntityPlaneFuelBase extends EntityPlaneControlBase implements IFluidHandler {

    private static final EntityDataAccessor<Integer> FUEL = SynchedEntityData.defineId(EntityPlaneFuelBase.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> FUEL_TYPE = SynchedEntityData.defineId(EntityPlaneFuelBase.class, EntityDataSerializers.STRING);

    public EntityPlaneFuelBase(EntityType type, Level worldIn) {
        super(type, worldIn);
    }

    public abstract float getMaxFuelUsage();

    public abstract int getMaxFuel();

    @Override
    public void tick() {
        super.tick();
        fuelTick();
    }

    public void fuelTick() {
        if (!isStarted()) {
            return;
        }

        if (level().getGameTime() % 20L == 0L) {
            int consumeAmount = Math.max((int) Math.ceil(getEngineSpeed() * getMaxFuelUsage()), 1);
            setFuel(Math.max(getFuel() - consumeAmount, 0));
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
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(FUEL, 0);
        entityData.define(FUEL_TYPE, "");
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        setFuel(compound.getInt("Fuel"));
        setFuelType(compound.getString("FuelType"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Fuel", getFuel());
        Fluid fuel = getFuelType();
        compound.putString("FuelType", fuel == null ? "" : ForgeRegistries.FLUIDS.getKey(fuel).toString());
    }

    @Nullable
    public Fluid getFuelType() {
        String type = entityData.get(FUEL_TYPE);
        if (type.isEmpty()) {
            return null;
        }
        return ForgeRegistries.FLUIDS.getValue(new ResourceLocation(type));
    }

    public void setFuelType(Fluid fluid) {
        setFuelType(ForgeRegistries.FLUIDS.getKey(fluid).toString());
    }

    public void setFuelType(String fluid) {
        entityData.set(FUEL_TYPE, fluid);
    }

    public int getFuel() {
        return entityData.get(FUEL);
    }

    public void setFuel(int fuel) {
        entityData.set(FUEL, fuel);
    }

    public boolean isValidFuel(FluidStack fluid) {
        return Main.SERVER_CONFIG.validFuels.contains(fluid.getFluid());
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {
        Fluid fluid = getFuelType();
        return new FluidStack(fluid == null ? Fluids.EMPTY : fluid, getFuel());
    }

    @Override
    public int getTankCapacity(int tank) {
        return getMaxFuel();
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        return isValidFuel(stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource == null || !isValidFuel(resource)) {
            return 0;
        }

        Fluid fluid = getFuelType();

        if (fluid != null && getFuel() > 0 && !resource.getFluid().equals(fluid)) {
            return 0;
        }

        int amount = Math.min(resource.getAmount(), getMaxFuel() - getFuel());

        if (action.execute()) {
            int i = getFuel() + amount;
            if (i > getMaxFuel()) {
                i = getMaxFuel();
            }
            setFuel(i);
            setFuelType(resource.getFluid());
        }

        return amount;
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        return FluidStack.EMPTY;
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        return FluidStack.EMPTY;
    }

}
