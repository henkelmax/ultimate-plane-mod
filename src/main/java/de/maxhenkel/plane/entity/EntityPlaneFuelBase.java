package de.maxhenkel.plane.entity;

import de.maxhenkel.plane.Config;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class EntityPlaneFuelBase extends EntityPlaneControlBase implements IFluidHandler {

    private static final DataParameter<Integer> FUEL = EntityDataManager.createKey(EntityPlaneControlBase.class, DataSerializers.VARINT);
    private static final DataParameter<String> FUEL_TYPE = EntityDataManager.createKey(EntityPlaneControlBase.class, DataSerializers.STRING);

    public EntityPlaneFuelBase(EntityType type, World worldIn) {
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

        if (world.getGameTime() % 20L == 0L) {
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
    protected void registerData() {
        super.registerData();
        dataManager.register(FUEL, 0);
        dataManager.register(FUEL_TYPE, "");
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        setFuel(compound.getInt("Fuel"));
        setFuelType(compound.getString("FuelType"));
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("Fuel", getFuel());
        Fluid fuel = getFuelType();
        compound.putString("FuelType", fuel == null ? "" : fuel.getRegistryName().toString());
    }

    @Nullable
    public Fluid getFuelType() {
        String type = dataManager.get(FUEL_TYPE);
        if (type.isEmpty()) {
            return null;
        }
        return ForgeRegistries.FLUIDS.getValue(new ResourceLocation(type));
    }

    public void setFuelType(Fluid fluid) {
        setFuelType(fluid.getRegistryName().toString());
    }

    public void setFuelType(String fluid) {
        dataManager.set(FUEL_TYPE, fluid);
    }

    public int getFuel() {
        return dataManager.get(FUEL);
    }

    public void setFuel(int fuel) {
        dataManager.set(FUEL, fuel);
    }

    public boolean isValidFuel(FluidStack fluid) {
        return Config.VALID_FUEL_LIST.contains(fluid.getFluid());
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
