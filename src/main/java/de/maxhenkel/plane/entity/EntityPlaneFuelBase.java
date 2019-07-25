package de.maxhenkel.plane.entity;

import de.maxhenkel.plane.FluidStackWrapper;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class EntityPlaneFuelBase extends EntityPlaneControlBase implements IFluidHandler {

    private static final DataParameter<Integer> FUEL = EntityDataManager.createKey(EntityPlaneControlBase.class,
            DataSerializers.VARINT);

    private static final float MAX_FUEL_USAGE = 5F;
    public static final int MAX_FUEL = 5000;

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

    public boolean isValidFuel(FluidStack fluid) {
        return fluid.getFluid().getName().equals("bio_diesel"); //TODO check for valid fuel
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        return new IFluidTankProperties[]{new IFluidTankProperties() {

            @Override
            public FluidStack getContents() {
                return new FluidStackWrapper(new Fluid("bio_diesel", null, null, 0), getFuel()); // TODO
            }

            @Override
            public int getCapacity() {
                return MAX_FUEL;
            }

            @Override
            public boolean canFillFluidType(FluidStack fluidStack) {
                return isValidFuel(fluidStack);
            }

            @Override
            public boolean canFill() {
                return true;
            }

            @Override
            public boolean canDrainFluidType(FluidStack fluidStack) {
                return false;
            }

            @Override
            public boolean canDrain() {
                return false;
            }
        }};
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if (resource == null || !isValidFuel(resource)) {
            return 0;
        }

        int amount = Math.min(resource.amount, MAX_FUEL - getFuel());

        if (doFill) {
            int i = getFuel() + amount;
            if (i > MAX_FUEL) {
                i = MAX_FUEL;
            }
            setFuel(i);
        }

        return amount;
    }

    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        return null;
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        return null;
    }
}
