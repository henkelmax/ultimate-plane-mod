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
        this.preventEntitySpawning = true;
        this.stepHeight = 0.6F;
    }

    @Override
    public void tick() {
        func_233577_ch_();

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
    protected boolean canFitPassenger(Entity passenger) {
        return this.getPassengers().size() < getPassengerSize();
    }

    private void tickLerp() {
        if (this.steps > 0 && !this.canPassengerSteer()) {
            double x = getPosX() + (clientX - getPosX()) / (double) steps;
            double y = getPosY() + (clientY - getPosY()) / (double) steps;
            double z = getPosZ() + (clientZ - getPosZ()) / (double) steps;
            double d3 = MathHelper.wrapDegrees(clientYaw - (double) rotationYaw);
            this.rotationYaw = (float) ((double) rotationYaw + d3 / (double) steps);
            this.rotationPitch = (float) ((double) rotationPitch + (clientPitch - (double) rotationPitch) / (double) steps);
            steps--;
            setPosition(x, y, z);
            setRotation(rotationYaw, rotationPitch);
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
        entityToUpdate.setRenderYawOffset(rotationYaw);
        float f = MathHelper.wrapDegrees(entityToUpdate.rotationYaw - rotationYaw);
        float f1 = MathHelper.clamp(f, -130.0F, 130.0F);
        entityToUpdate.prevRotationYaw += f1 - f;
        entityToUpdate.rotationYaw += f1 - f;
        entityToUpdate.setRotationYawHead(entityToUpdate.rotationYaw);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void applyOrientationToEntity(Entity entityToUpdate) {
        this.applyOriantationsToEntity(entityToUpdate);
    }

    public abstract Vector3d[] getPlayerOffsets();

    @Override
    public void updatePassenger(Entity passenger) {
        if (!isPassenger(passenger)) {
            return;
        }

        List<Entity> passengers = getPassengers();

        if (passengers.size() > 0) {
            int i = passengers.indexOf(passenger);

            Vector3d offset = getPlayerOffsets()[i];
            offset = offset.rotateYaw((float) -Math.toRadians(rotationYaw));

            passenger.setPosition(getPosX() + offset.x, getPosY() + offset.y, getPosZ() + offset.z);
            passenger.rotationYaw += deltaRotation;
            passenger.setRotationYawHead(passenger.getRotationYawHead() + deltaRotation);
        }

        applyOriantationsToEntity(passenger);
    }

    @Override
    public Entity getControllingPassenger() {
        return null;
    }

    @Override
    public boolean canCollide(Entity entity) {
        return false;
    }

    @Override
    public boolean func_241845_aY() {
        return false;
    }

    @Override
    public boolean canBePushed() {
        return false;
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
    public ActionResultType processInitialInteract(PlayerEntity player, Hand hand) {
        if (!player.isSneaking()) {
            if (player.getRidingEntity() != this) {
                if (!world.isRemote) {
                    player.startRiding(this);
                }
            }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.FAIL;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

}
