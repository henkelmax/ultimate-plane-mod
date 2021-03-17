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
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.network.NetworkHooks;

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
        this.blocksBuilding = true;
        this.maxUpStep = 0.6F;
    }

    @Override
    public void tick() {
        checkAndResetForcedChunkAdditionFlag();

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
        List<Entity> passengers;
        try {
            passengers = ObfuscationReflectionHelper.getPrivateValue(Entity.class, this, "field_184244_h");
        } catch (ObfuscationReflectionHelper.UnableToFindFieldException x1) {
            try {
                passengers = ObfuscationReflectionHelper.getPrivateValue(Entity.class, this, "passengers");
            } catch (ObfuscationReflectionHelper.UnableToFindFieldException e) {
                super.addPassenger(passenger);
                e.printStackTrace();
                return;
            }
        }
        passengers.add(passenger);
    }

    public abstract int getPassengerSize();

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengers().size() < getPassengerSize();
    }

    private void tickLerp() {
        if (this.steps > 0 && !this.isControlledByLocalInstance()) {
            double x = getX() + (clientX - getX()) / (double) steps;
            double y = getY() + (clientY - getY()) / (double) steps;
            double z = getZ() + (clientZ - getZ()) / (double) steps;
            double d3 = MathHelper.wrapDegrees(clientYaw - (double) yRot);
            this.yRot = (float) ((double) yRot + d3 / (double) steps);
            this.xRot = (float) ((double) xRot + (clientPitch - (double) xRot) / (double) steps);
            steps--;
            setPos(x, y, z);
            setRot(yRot, xRot);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
        this.clientX = x;
        this.clientY = y;
        this.clientZ = z;
        this.clientYaw = yaw;
        this.clientPitch = pitch;
        this.steps = 10;
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
        return null;
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
