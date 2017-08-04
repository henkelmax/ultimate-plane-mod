package de.maxhenkel.plane.entity.plane;

import de.maxhenkel.car.entity.car.base.EntityVehicleBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityPlaneBase extends EntityVehicleBase{

	public EntityPlaneBase(World worldIn) {
		super(worldIn);
		setSize(2.5F, 1.3F);
	}

	@Override
	protected void entityInit() {
		
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		
	}
	
	@Override
	public double getMountedYOffset() {
		return -0.45;
	}

}
