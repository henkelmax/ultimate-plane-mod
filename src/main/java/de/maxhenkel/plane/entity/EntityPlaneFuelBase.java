package de.maxhenkel.plane.entity;

import de.maxhenkel.plane.Main;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class EntityPlaneFuelBase extends EntityPlaneControlBase implements IFluidHandler {

    private static final EntityDataAccessor<Integer> FUEL = SynchedEntityData.defineId(EntityPlaneFuelBase.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> FUEL_TYPE = SynchedEntityData.defineId(EntityPlaneFuelBase.class, EntityDataSerializers.STRING);

    private float fuelAccumulator;

    public EntityPlaneFuelBase(EntityType type, Level worldIn) {
        super(type, worldIn);
    }

    public abstract int getFuelCapacity();

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide()) {
            fuelTick();
        }
    }

    protected void fuelTick() {
        if (!isStarted()) {
            return;
        }

        setFuel(Math.max(getFuel() - calculateFuelUsage(), 0));
    }

    protected int calculateFuelUsage() {
        float maxUsage = getBaseFuelUsage() * getFuelMultiplier();

        float idleUsage = Math.max(Math.min(getIdleUsageMultiplier(), 1F), 0F);

        maxUsage = (maxUsage * idleUsage) + (maxUsage * getEngineSpeed()) * (1F - idleUsage);

        fuelAccumulator += maxUsage;

        int fuelInt = (int) fuelAccumulator;
        fuelAccumulator -= fuelInt;
        return fuelInt;
    }

    protected abstract float getBaseFuelUsage();

    protected float getFuelMultiplier() {
        return 1F;
    }

    protected float getIdleUsageMultiplier() {
        return 0.1F;
    }

    @Override
    public boolean canEngineBeStarted() {
        if (getFuel() <= 0) {
            return false;
        }
        return super.canEngineBeStarted();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(FUEL, 0);
        builder.define(FUEL_TYPE, "");
    }

    @Override
    public void readAdditionalSaveData(ValueInput valueInput) {
        super.readAdditionalSaveData(valueInput);
        setFuel(valueInput.getIntOr("Fuel", 0));
        setFuelType(valueInput.getStringOr("FuelType", ""));
    }

    @Override
    public void addAdditionalSaveData(ValueOutput valueOutput) {
        super.addAdditionalSaveData(valueOutput);
        valueOutput.putInt("Fuel", getFuel());
        Fluid fuel = getFuelType();
        valueOutput.putString("FuelType", fuel == null ? "" : BuiltInRegistries.FLUID.getKey(fuel).toString());
    }

    @Nullable
    public Fluid getFuelType() {
        String type = entityData.get(FUEL_TYPE);
        if (type.isEmpty()) {
            return null;
        }
        ResourceLocation resourceLocation = ResourceLocation.tryParse(type);
        if (resourceLocation == null) {
            return null;
        }
        return BuiltInRegistries.FLUID.get(resourceLocation).map(Holder.Reference::value).orElse(null);
    }

    public void setFuelType(Fluid fluid) {
        setFuelType(BuiltInRegistries.FLUID.getKey(fluid).toString());
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
        return Main.SERVER_CONFIG.validFuels.stream().anyMatch(fluidTag -> fluidTag.contains(fluid.getFluid()));
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
        return getFuelCapacity();
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

        int amount = Math.min(resource.getAmount(), getFuelCapacity() - getFuel());

        if (action.execute()) {
            int i = getFuel() + amount;
            if (i > getFuelCapacity()) {
                i = getFuelCapacity();
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
