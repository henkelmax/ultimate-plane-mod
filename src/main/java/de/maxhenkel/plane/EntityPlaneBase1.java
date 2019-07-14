package de.maxhenkel.plane;

import de.maxhenkel.plane.entity.EntityPlanePart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.List;

public class EntityPlaneBase1 extends Entity {

    protected float deltaRotation;
    private EntityPlanePart[] parts;

    public EntityPlaneBase1(World world) {
        this(Main.PLANE_ENTITY_TYPE, world);
    }

    public EntityPlaneBase1(EntityType<?> type, World world) {
        super(type, world);
       /* parts = new EntityPlanePart[]{
                new EntityPlanePart(this, 1F, 1F),
                new EntityPlanePart(this, 1F, 1F)
        };*/
    }

    @Override
    public void tick() {
        super.tick();

        Vec3d[] positions = new Vec3d[this.parts.length];

        for (int j = 0; j < this.parts.length; j++) {
            positions[j] = new Vec3d(this.parts[j].posX, this.parts[j].posY, this.parts[j].posZ);
        }

        if (parts.length >= 2) {
            parts[0].tick();
            parts[0].setLocationAndAngles(this.posX - 1, this.posY, this.posZ - 1, 0.0F, 0.0F);

            parts[1].tick();
            parts[1].setLocationAndAngles(this.posX - 1, this.posY, this.posZ - 1, 0.0F, 0.0F);
        }

        for (int l = 0; l < this.parts.length; l++) {
            this.parts[l].prevPosX = positions[l].x;
            this.parts[l].prevPosY = positions[l].y;
            this.parts[l].prevPosZ = positions[l].z;
        }
    }

    @Override
    public boolean processInitialInteract(PlayerEntity player, Hand hand) {
        if (!player.isSneaking()) {
            if (player.getRidingEntity() != this) {
                if (!world.isRemote) {
                    player.startRiding(this);
                }
            }
            return true;
        }
        return false;
    }

    public int getPassengerSize() {
        return 1;
    }

    public Vec3d[] getPlayerOffsets() {
        return new Vec3d[]{new Vec3d(0D, 0D, 0D)};
    }

    @Override
    public void updatePassenger(Entity passenger) {
        if (!isPassenger(passenger)) {
            return;
        }

        double front = 0.0F;
        double side = 0.0F;
        double height = 0.0F;

        List<Entity> passengers = getPassengers();

        if (passengers.size() > 0) {
            int i = passengers.indexOf(passenger);

            Vec3d offset = getPlayerOffsets()[i];
            front = offset.x;
            side = offset.z;
            height = offset.y;
        }

        Vec3d vec3d = (new Vec3d(front, height, side))
                .rotateYaw(-this.rotationYaw * 0.017453292F - ((float) Math.PI / 2F));
        passenger.setPosition(this.posX + vec3d.x, this.posY + vec3d.y, this.posZ + vec3d.z);
        passenger.rotationYaw += this.deltaRotation;
        passenger.setRotationYawHead(passenger.getRotationYawHead() + this.deltaRotation);
        this.applyYawToEntity(passenger);
    }

    protected void applyYawToEntity(Entity entityToUpdate) {
        entityToUpdate.setRenderYawOffset(this.rotationYaw);
        float f = MathHelper.wrapDegrees(entityToUpdate.rotationYaw - this.rotationYaw);
        float f1 = MathHelper.clamp(f, -130.0F, 130.0F);
        entityToUpdate.prevRotationYaw += f1 - f;
        entityToUpdate.rotationYaw += f1 - f;
        entityToUpdate.setRotationYawHead(entityToUpdate.rotationYaw);
    }

    /**
     * Applies this entity's orientation (pitch/yaw) to another entity. Used to
     * update passenger orientation.
     */
    @OnlyIn(Dist.CLIENT)
    public void applyOrientationToEntity(Entity entityToUpdate) {
        this.applyYawToEntity(entityToUpdate);
    }

    @Override
    protected boolean canFitPassenger(Entity passenger) {
        return this.getPassengers().size() < getPassengerSize();
    }

    @Override
    protected void registerData() {

    }

    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {

    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {

    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
