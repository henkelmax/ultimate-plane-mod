package de.maxhenkel.plane.entity;

import de.maxhenkel.plane.PlaneMod;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import javax.annotation.Nullable;
import java.util.AbstractMap;
import java.util.Map;

public abstract class EntityPlaneFuelBase extends EntityPlaneControlBase implements ResourceHandler<FluidResource> {

    private static final EntityDataAccessor<Integer> FUEL = SynchedEntityData.defineId(EntityPlaneFuelBase.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> FUEL_TYPE = SynchedEntityData.defineId(EntityPlaneFuelBase.class, EntityDataSerializers.STRING);

    private float fuelAccumulator;

    private final SnapshotJournal<Map.Entry<Fluid, Integer>> fluidJournal = new SnapshotJournal<>() {
        @Override
        protected Map.Entry<Fluid, Integer> createSnapshot() {
            return new AbstractMap.SimpleEntry<>(getFuelType(), getFuel());
        }

        @Override
        protected void revertToSnapshot(Map.Entry<Fluid, Integer> snapshot) {
            Fluid fluid = snapshot.getKey();
            if (fluid == null) {
                setFuelType("");
            } else {
                setFuelType(fluid);
            }
            setFuel(snapshot.getValue());
        }
    };

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
        Identifier resourceLocation = Identifier.tryParse(type);
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

    public boolean isValidFuel(Fluid fluid) {
        return PlaneMod.SERVER_CONFIG.validFuels.stream().anyMatch(fluidTag -> fluidTag.contains(fluid));
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public FluidResource getResource(int index) {
        Fluid fluid = getFuelType();
        return fluid == null ? FluidResource.EMPTY : FluidResource.of(fluid);
    }

    @Override
    public long getAmountAsLong(int index) {
        return getFuel();
    }

    @Override
    public long getCapacityAsLong(int index, FluidResource resource) {
        return getFuelCapacity();
    }

    @Override
    public boolean isValid(int index, FluidResource resource) {
        return isValidFuel(resource.getFluid());
    }

    @Override
    public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
        if (!isValidFuel(resource.getFluid())) {
            return 0;
        }

        Fluid fluid = getFuelType();

        if (fluid != null && getFuel() > 0 && !resource.getFluid().equals(fluid)) {
            return 0;
        }

        int result = Math.min(amount, getFuelCapacity() - getFuel());

        fluidJournal.updateSnapshots(transaction);
        int i = getFuel() + result;
        if (i > getFuelCapacity()) {
            i = getFuelCapacity();
        }
        setFuel(i);
        setFuelType(resource.getFluid());

        return result;
    }

    @Override
    public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
        return 0;
    }

}
