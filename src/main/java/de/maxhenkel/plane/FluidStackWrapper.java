package de.maxhenkel.plane;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.IRegistryDelegate;

import java.lang.reflect.Field;

public class FluidStackWrapper extends FluidStack {

    private Fluid fluid;

    public FluidStackWrapper(Fluid fluid, int amount) {
        super(fluid, amount);
        setFluid(fluid);
    }

    public FluidStackWrapper(FluidStack stack, int amount) {
        super(stack, amount);
        setFluid(fluid);
    }

    public FluidStackWrapper(Fluid fluid, int amount, CompoundNBT nbt) {
        super(fluid, amount, nbt);
        setFluid(fluid);
    }

    private void setFluid(Fluid fluid) {
        try {
            Field fluidDelegate = FluidStack.class.getDeclaredField("fluidDelegate");

            fluidDelegate.setAccessible(true);
            fluidDelegate.set(this, new IRegistryDelegate<Fluid>() {

                @Override
                public Fluid get() {
                    return fluid;
                }

                @Override
                public ResourceLocation name() {
                    return new ResourceLocation(Main.MODID, fluid.getName());
                }

                @Override
                public Class<Fluid> type() {
                    return Fluid.class;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getLocalizedName() {
        return this.getFluid().getLocalizedName(this);
    }

    public String getUnlocalizedName() {
        return this.getFluid().getUnlocalizedName(this);
    }
}
