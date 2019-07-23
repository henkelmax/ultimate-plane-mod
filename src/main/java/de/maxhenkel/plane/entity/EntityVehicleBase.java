package de.maxhenkel.plane.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

public abstract class EntityVehicleBase extends Entity {

    private int steps;
    private double clientX;
    private double clientY;
    private double clientZ;
    private double clientYaw;
    private double clientPitch;

    protected float deltaRotation;

    public EntityVehicleBase(EntityType type, World worldIn) {
        super(type, worldIn);
        this.preventEntitySpawning = true;
        this.stepHeight = 0.6F;
    }

    @Override
    public void tick() {
        setPositionNonDirty();

        super.tick();
        this.tickLerp();
    }

    @Override
    protected void removePassenger(Entity passenger) {
        super.removePassenger(passenger);
        if (passenger instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) passenger;
            Direction facing = getHorizontalFacing();

            double offsetX = 0;
            double offsetZ = 0;

            for (int i = 0; i < 4; i++) {
                AxisAlignedBB playerbb = player.getBoundingBox();
                double playerHitboxWidth = (playerbb.maxX - playerbb.minX) / 2;
                double carHitboxWidth = getBoundingBox().getZSize() / 2;

                double offset = playerHitboxWidth + carHitboxWidth + 0.2;

                offsetX += facing.getXOffset() * offset;
                offsetZ += facing.getZOffset() * offset;

                AxisAlignedBB aabb = player.getBoundingBox().offset(offsetX, 0, offsetZ);

                if (!world.checkBlockCollision(aabb)) {
                    break;
                }

                offsetX = 0;
                offsetZ = 0;
                facing = facing.rotateY();
            }

            player.setPositionAndUpdate(posX + offsetX, posY, posZ + offsetZ);
        }
    }

    public PlayerEntity getDriver() {
        List<Entity> passengers = getPassengers();
        if (passengers.size() <= 0) {
            return null;
        }

        if (passengers.get(0) instanceof PlayerEntity) {
            return (PlayerEntity) passengers.get(0);
        }

        return null;
    }

    public abstract int getPassengerSize();

    @Override
    protected boolean canFitPassenger(Entity passenger) {
        return this.getPassengers().size() < getPassengerSize();
    }

    private void tickLerp() {
        if (this.steps > 0 && !this.canPassengerSteer()) {
            double x = posX + (clientX - posX) / (double) steps;
            double y = posY + (clientY - posY) / (double) steps;
            double z = posZ + (clientZ - posZ) / (double) steps;
            double d3 = MathHelper.wrapDegrees(clientYaw - (double) rotationYaw);
            this.rotationYaw = (float) ((double) rotationYaw + d3 / (double) steps);
            this.rotationPitch = (float) ((double) rotationPitch
                    + (clientPitch - (double) rotationPitch) / (double) steps);
            steps--;
            setPosition(x, y, z);
            setRotation(rotationYaw, rotationPitch);
        }
    }

    /**
     * Set the position and rotation values directly without any clamping.
     */
    @OnlyIn(Dist.CLIENT)
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch,
                                             int posRotationIncrements, boolean teleport) {
        this.clientX = x;
        this.clientY = y;
        this.clientZ = z;
        this.clientYaw = (double) yaw;
        this.clientPitch = (double) pitch;
        this.steps = 10;
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

    public abstract Vec3d[] getPlayerOffsets();

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

    @Override
    public Entity getControllingPassenger() {
        return null;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBox(Entity entityIn) {
        return null;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox() {
        return getBoundingBox();
    }

    @Override
    public boolean canBePushed() {
        return true;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
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

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
