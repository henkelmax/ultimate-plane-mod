package de.maxhenkel.plane.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.List;

public abstract class EntityVehicleBase extends Entity {

    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;
    private double lerpYRot;
    private double lerpXRot;

    protected float deltaRotation;

    public EntityVehicleBase(EntityType type, World worldIn) {
        super(type, worldIn);
        this.blocksBuilding = true;
        this.maxUpStep = 0.6F;
    }

    @Override
    public void tick() {
        super.tick();
        this.tickLerp();
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

    @Override
    protected void addPassenger(Entity passenger) {
        passengers.add(passenger);

        if (isControlledByLocalInstance() && lerpSteps > 0) {
            lerpSteps = 0;
            absMoveTo(lerpX, lerpY, lerpZ, (float) lerpYRot, (float) lerpXRot);
        }
    }

    public abstract int getPassengerSize();

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengers().size() < getPassengerSize();
    }

    private void tickLerp() {
        if (isControlledByLocalInstance()) {
            lerpSteps = 0;
            setPacketCoordinates(this.getX(), this.getY(), this.getZ());
        }

        if (lerpSteps > 0) {
            double x = getX() + (lerpX - getX()) / (double) lerpSteps;
            double y = getY() + (lerpY - getY()) / (double) lerpSteps;
            double z = getZ() + (lerpZ - getZ()) / (double) lerpSteps;
            double ry = MathHelper.wrapDegrees(lerpYRot - (double) yRot);
            yRot = (float) ((double) yRot + ry / (double) lerpSteps);
            xRot = (float) ((double) xRot + (lerpXRot - (double) xRot) / (double) lerpSteps);
            --lerpSteps;
            setPos(x, y, z);
            setRot(yRot, xRot);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void lerpTo(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
        this.lerpX = x;
        this.lerpY = y;
        this.lerpZ = z;
        this.lerpYRot = yaw;
        this.lerpXRot = pitch;
        this.lerpSteps = 10;
    }

    protected void applyOriantationsToEntity(Entity entityToUpdate) {
        entityToUpdate.setYBodyRot(yRot);
        float f = MathHelper.wrapDegrees(entityToUpdate.yRot - yRot);
        float f1 = MathHelper.clamp(f, -130.0F, 130.0F);
        entityToUpdate.yRotO += f1 - f;
        entityToUpdate.yRot += f1 - f;
        entityToUpdate.setYHeadRot(entityToUpdate.yRot);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onPassengerTurned(Entity entityToUpdate) {
        this.applyOriantationsToEntity(entityToUpdate);
    }

    public abstract Vector3d[] getPlayerOffsets();

    @Override
    public void positionRider(Entity passenger) {
        if (!hasPassenger(passenger)) {
            return;
        }

        List<Entity> passengers = getPassengers();

        if (passengers.size() > 0) {
            int i = passengers.indexOf(passenger);

            Vector3d offset = getPlayerOffsets()[i];
            offset = offset.yRot((float) -Math.toRadians(yRot));

            passenger.setPos(getX() + offset.x, getY() + offset.y, getZ() + offset.z);
            passenger.yRot += deltaRotation;
            passenger.setYHeadRot(passenger.getYHeadRot() + deltaRotation);
        }

        applyOriantationsToEntity(passenger);
    }

    @Override
    public Entity getControllingPassenger() {
        return getDriver();
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    protected boolean isMovementNoisy() {
        return false;
    }

    @Override
    public ActionResultType interact(PlayerEntity player, Hand hand) {
        if (!player.isShiftKeyDown()) {
            if (player.getVehicle() != this) {
                if (!level.isClientSide) {
                    player.startRiding(this);
                }
            }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.FAIL;
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

}
