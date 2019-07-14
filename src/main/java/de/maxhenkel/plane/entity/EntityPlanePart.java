package de.maxhenkel.plane.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.Pose;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.world.ServerWorld;

public class EntityPlanePart extends Entity {
    public final EntityPlaneBase plane;
    private final EntitySize size;
    private boolean isInitialized;

    public EntityPlanePart(EntityPlaneBase plane, float width, float height) {
        super(plane.getType(), plane.world);
        this.size = EntitySize.flexible(width, height);
        this.recalculateSize();
        this.plane = plane;
    }

    @Override
    public void tick() {
        super.tick();

        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld) world;
            if (!isInitialized) {
                //setEntityId(plane.getEntityId());
                //boolean added = serverWorld.addEntityIfNotDuplicate(this);
                //System.out.println("Added entity: " + added);
                isInitialized = true;
            }
        }
    }

    protected void registerData() {
    }

    protected void readAdditional(CompoundNBT compound) {
    }

    protected void writeAdditional(CompoundNBT compound) {
    }

    public boolean canBeCollidedWith() {
        return true;
    }

    public boolean attackEntityFrom(DamageSource source, float damage) {
        return plane.attackEntityFrom(source, damage);
    }

    public boolean isEntityEqual(Entity entity) {
        return this == entity || plane == entity;
    }

    public IPacket<?> createSpawnPacket() {
        throw new UnsupportedOperationException();
    }

    public EntitySize getSize(Pose pose) {
        return size;
    }
}
