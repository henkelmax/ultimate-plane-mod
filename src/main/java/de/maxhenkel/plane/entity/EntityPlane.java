package de.maxhenkel.plane.entity;

import de.maxhenkel.plane.Main;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

public class EntityPlane extends EntityPlaneSoundBase {

    public static final float SCALE_FACTOR = 0.8F;

    public EntityPlane(World world) {
        this(Main.PLANE_ENTITY_TYPE, world);
    }

    public EntityPlane(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
    }
}
