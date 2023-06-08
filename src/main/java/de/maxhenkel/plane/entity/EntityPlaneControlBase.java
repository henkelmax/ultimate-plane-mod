package de.maxhenkel.plane.entity;

import de.maxhenkel.plane.Main;
import de.maxhenkel.plane.net.MessageControlPlane;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class EntityPlaneControlBase extends EntityPlaneDamageBase {

    private static final EntityDataAccessor<Float> ENGINE_SPEED = SynchedEntityData.defineId(EntityPlaneControlBase.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> STARTED = SynchedEntityData.defineId(EntityPlaneControlBase.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> START_TIME = SynchedEntityData.defineId(EntityPlaneControlBase.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> THRUST_POSITIVE = SynchedEntityData.defineId(EntityPlaneControlBase.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> THRUST_NEGATIVE = SynchedEntityData.defineId(EntityPlaneControlBase.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> LEFT = SynchedEntityData.defineId(EntityPlaneControlBase.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> RIGHT = SynchedEntityData.defineId(EntityPlaneControlBase.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> UP = SynchedEntityData.defineId(EntityPlaneControlBase.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DOWN = SynchedEntityData.defineId(EntityPlaneControlBase.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> BRAKE = SynchedEntityData.defineId(EntityPlaneControlBase.class, EntityDataSerializers.BOOLEAN);

    public static final double MAX_ENGINE_SPEED = 1.5D;
    public static final double ENGINE_ACCELERATION = 0.005D;
    public static final double BRAKE_POWER = 0.012D;

    public EntityPlaneControlBase(EntityType type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    public void tick() {
        super.tick();

        if (getStartTime() > getTimeToStart() && !isStarted()) {
            if (canEngineBeStarted()) {
                setStarted(true);
            }
        }

        if (isStarted() && !canEngineBeStarted()) {
            setStarted(false);
        }

        if (!level().isClientSide) {
            if (isStarted()) {
                if (isThrustPositive()) {
                    setEngineSpeed(Math.min(getEngineSpeed() + 0.025F, 1F));
                } else if (isThrustNegative()) {
                    setEngineSpeed(Math.max(getEngineSpeed() - 0.025F, 0F));
                }
            } else {
                setEngineSpeed(0F);
            }
        }


        controlPlane();
        handleRotation();
    }

    public boolean canEngineBeStarted() {
        if (isStarted() && getPlaneDamage() >= 100F) {
            return false;
        }
        return true;
    }

    public int getTimeToStart() {
        int time = 40;
        time += ((int) getPlaneDamage() / 2F);
        return time;
    }

    private void handleRotation() {

        double speed = getDeltaMovement().length();

        float rotationSpeed = 0;
        if (Math.abs(speed) > 0F) {
            rotationSpeed = Mth.abs(0.5F / (float) Math.pow(speed, 2)); //rotation modifier+0.5
            rotationSpeed = Mth.clamp(rotationSpeed, 1.0F, 5.0F);
        }

        deltaRotation = 0;

        rotationSpeed = Math.abs(rotationSpeed);

        if (isLeft()) {
            deltaRotation -= rotationSpeed;
        }
        if (isRight()) {
            deltaRotation += rotationSpeed;
        }

        // ----- YAW ------
        setYRot(getYRot() + deltaRotation);
        float delta = Math.abs(getYRot() - yRotO);
        while (getYRot() > 180F) {
            setYRot(getYRot() - 360F);
            yRotO = getYRot() - delta;
        }
        while (getYRot() <= -180F) {
            setYRot(getYRot() + 360F);
            yRotO = delta + getYRot();
        }
        // ----- YAW ------

        // ----- PITCH ------
        if (isUp()) {
            setXRot(getXRot() - 1F);
        } else if (isDown()) {
            setXRot(getXRot() + 1F);
        }

        setXRot(Math.max(getXRot(), -90F));
        setXRot(Math.min(getXRot(), 90F));

        float groundPitchTolerance = 7F;

        if (isCollidedVertical()) {
            // Prevent leaning forwards when on ground
            if (getXRot() > 0) {
                setXRot(getXRot() - 10F);
                if (getXRot() < 0) {
                    setXRot(0);
                }
            }

            if (getXRot() < -groundPitchTolerance) {
                setXRot(getXRot() + 10F);
                if (getXRot() > -groundPitchTolerance) {
                    setXRot(getXRot() - groundPitchTolerance);
                }
            }
        }
        // ----- PITCH ------
    }

    private void controlPlane() {

        if (!isVehicle()) {
            setThrustPositive(false);
            setThrustNegative(false);
            setLeft(false);
            setRight(false);
            setUp(false);
            setDown(false);
            setBrake(true);
            setStartTime(0);
        }

        Vec3 motionVector = getDeltaMovement();
        double verticalMotion = Math.abs(motionVector.y);
        double horizontalMotion = getHorizontalMotion(motionVector);
        float engineSpeed = getEngineSpeed();

        if (isCollidedVertical()) {
            double speed = getDeltaMovement().length();
            double maxEngineSpeed = MAX_ENGINE_SPEED * engineSpeed;

            if (speed < maxEngineSpeed) {
                speed = Math.min(speed + engineSpeed * ENGINE_ACCELERATION, maxEngineSpeed);
            }

            if (isBrake()) {
                speed = decreaseToZero(speed, (1D / (speed + 1D)) * BRAKE_POWER); // brake resistance
            }

            if (engineSpeed <= 0F) {
                speed = decreaseToZero(speed, 0.002D); // ground resistance
            }

            Vec3 motion = getLookAngle().normalize().scale(speed).multiply(1D, 0D, 1D);
            setDeltaMovement(motion);
            if (speed > 0D) {
                move(MoverType.SELF, getDeltaMovement());
            }
        } else {
            double fallSpeed = getFallSpeed();
            Vec3 lookVec = getLookAngle();
            float modifiedPitch = (getXRot() < 0F ? getXRot() : Math.min(getXRot() * 1.5F, 90F)) - 5F;
            float pitch = modifiedPitch * ((float) Math.PI / 180F);
            double horizontalLook = Math.sqrt(lookVec.x * lookVec.x + lookVec.z * lookVec.z);

            double lookLength = lookVec.length();
            float cosPitch = Mth.cos(pitch);
            cosPitch = (float) ((double) cosPitch * (double) cosPitch * Math.min(1D, lookLength / 0.4D));
            motionVector = getDeltaMovement().add(0D, fallSpeed * (-1D + (double) cosPitch * 0.75D), 0D);
            if (motionVector.y < 0D && horizontalLook > 0D) {
                double down = motionVector.y * -0.1D * (double) cosPitch;
                motionVector = motionVector.add(lookVec.x * down / horizontalLook, down, lookVec.z * down / horizontalLook);
            }

            if (pitch < 0.0F && horizontalLook > 0D) {
                double d13 = horizontalMotion * (double) (-Mth.sin(pitch)) * 0.04D;
                motionVector = motionVector.add(-lookVec.x * d13 / horizontalLook, d13 * 3.2D, -lookVec.z * d13 / horizontalLook);
            }

            if (horizontalLook > 0D) {
                motionVector = motionVector.add((lookVec.x / horizontalLook * horizontalMotion - motionVector.x) * 0.1D, 0D, (lookVec.z / horizontalLook * horizontalMotion - motionVector.z) * 0.1D);
            }

            motionVector = motionVector.multiply(0.99D, 0.98D, 0.99D);

            double speed = motionVector.length();

            if (speed < MAX_ENGINE_SPEED * engineSpeed) {
                double addSpeed = 0D;
                addSpeed = addSpeed + engineSpeed * ENGINE_ACCELERATION * 4F;
                if (speed + addSpeed > MAX_ENGINE_SPEED * engineSpeed) {
                    addSpeed = (MAX_ENGINE_SPEED * engineSpeed) - speed;
                }
                if (addSpeed < 0D) {
                    addSpeed = 0D;
                }

                if (getPitchPercentage() < -0.25F) {
                    addSpeed = 0D;
                }

                Vec3 addVec = getLookAngle().normalize().scale(addSpeed);

                motionVector = motionVector.add(new Vec3(addVec.x, 0D, addVec.z));
            }

            if (isStalling(motionVector)) {
                motionVector = motionVector.multiply(new Vec3(0.975D, 1.025D, 0.975D));
            }

            setDeltaMovement(motionVector);

            move(MoverType.SELF, getDeltaMovement());
        }

        if (level().isClientSide) {
            return;
        }
        if (isCollidedHorizontal()) {
            double newHorizontalMotion = getHorizontalMotion(getDeltaMovement());
            double motionDifference = horizontalMotion - newHorizontalMotion;
            double damage = motionDifference * 1000D - 150D;
            if (damage > 0D) {
                damagePlane(damage, true);
            }
        }

        if (isCollidedVertical()) {
            double newVerticalMotion = Math.abs(getDeltaMovement().y);
            double motionDifference = verticalMotion - newVerticalMotion;
            double damage = motionDifference * 1000D - 100D;
            if (damage > 0D) {
                damagePlane(damage, false);
            }
        }
    }

    protected boolean isStalling(Vec3 motionVector) {
        return motionVector.multiply(1D, 0D, 1D).length() / 4D < -motionVector.y;
    }

    public abstract double getFallSpeed();

    @Override
    public void damagePlane(double damage, boolean horizontal) {
        super.damagePlane(damage, horizontal);
        if ((horizontal && damage > 5D) || damage > 20D) {
            setStarted(false);
        }
    }

    private boolean onGroundLast;
    private boolean onGroundLast2;

    public boolean isCollidedVertical() {
        boolean last = onGroundLast;
        boolean last2 = onGroundLast2;
        onGroundLast2 = onGroundLast;
        onGroundLast = onGround();
        return last || last2 || onGround();
    }

    public boolean isCollidedHorizontal() {
        return horizontalCollision;
    }

    public double getHorizontalMotion(Vec3 vec3d) {
        return Math.sqrt(vec3d.x * vec3d.x + vec3d.z * vec3d.z);
    }

    public double getAngle(Vec3 vec1, Vec3 vec2) {
        return Math.acos(Math.abs(vec1.dot(vec2)) / (vec1.length() * vec2.length()));
    }

    private static double decreaseToZero(double num, double amount) {
        double erg;
        if (num < 0) {
            erg = num + amount;
            if (erg > 0) {
                erg = 0;
            }
        } else {
            erg = num - amount;
            if (erg < 0) {
                erg = 0;
            }
        }

        return erg;
    }

    public float getPitchPercentage() {
        return getXRot() / 90F;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(STARTED, false);
        entityData.define(START_TIME, 0);
        entityData.define(ENGINE_SPEED, 0F);
        entityData.define(THRUST_POSITIVE, false);
        entityData.define(THRUST_NEGATIVE, false);
        entityData.define(LEFT, false);
        entityData.define(RIGHT, false);
        entityData.define(UP, false);
        entityData.define(DOWN, false);
        entityData.define(BRAKE, false);
    }

    public void updateControls(boolean up, boolean down, boolean thrustPos, boolean thrustNeg, boolean left, boolean right, boolean braking, boolean starting) {
        boolean needsUpdate = false;

        if (isThrustPositive() != thrustPos) {
            setThrustPositive(thrustPos);
            needsUpdate = true;
        }

        if (isThrustNegative() != thrustNeg) {
            setThrustNegative(thrustNeg);
            needsUpdate = true;
        }

        if (isLeft() != left) {
            setLeft(left);
            needsUpdate = true;
        }

        if (isRight() != right) {
            setRight(right);
            needsUpdate = true;
        }

        if (isUp() != up) {
            setUp(up);
            needsUpdate = true;
        }

        if (isDown() != down) {
            setDown(down);
            needsUpdate = true;
        }

        if (isBrake() != braking) {
            setBrake(braking);
            needsUpdate = true;
        }


        if (starting) {
            if (isStarted()) {
                if (getStartTime() <= 0) {
                    setStarted(false);
                }
            } else {
                setStartTime(getStartTime() + 1);
            }
            needsUpdate = true;
        } else {
            if (getStartTime() > 0) {
                setStartTime(0);
                needsUpdate = true;
            }
        }

        if (level().isClientSide && needsUpdate) {
            Main.SIMPLE_CHANNEL.sendToServer(new MessageControlPlane(up, down, thrustPos, thrustNeg, left, right, braking, starting));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("EngineSpeed", getEngineSpeed());
        compound.putBoolean("Started", isStarted());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        setEngineSpeed(compound.getFloat("EngineSpeed"));
        ((EntityPlaneSoundBase) this).setStarted(compound.getBoolean("Started"), false);
    }

    public boolean isStarted() {
        return entityData.get(STARTED);
    }

    public void setStarted(boolean started) {
        entityData.set(STARTED, started);
    }

    public boolean isBrake() {
        return entityData.get(BRAKE);
    }

    public void setBrake(boolean breaking) {
        entityData.set(BRAKE, breaking);
    }

    public boolean isThrustPositive() {
        return entityData.get(THRUST_POSITIVE);
    }

    public void setThrustPositive(boolean thrustPositive) {
        entityData.set(THRUST_POSITIVE, thrustPositive);
    }

    public boolean isThrustNegative() {
        return entityData.get(THRUST_NEGATIVE);
    }

    public void setThrustNegative(boolean thrustNegative) {
        entityData.set(THRUST_NEGATIVE, thrustNegative);
    }

    public boolean isLeft() {
        return entityData.get(LEFT);
    }

    public void setLeft(boolean left) {
        entityData.set(LEFT, left);
    }

    public boolean isRight() {
        return entityData.get(RIGHT);
    }

    public void setRight(boolean right) {
        entityData.set(RIGHT, right);
    }

    public boolean isUp() {
        return entityData.get(UP);
    }

    public void setUp(boolean up) {
        entityData.set(UP, up);
    }

    public boolean isDown() {
        return entityData.get(DOWN);
    }

    public void setDown(boolean down) {
        entityData.set(DOWN, down);
    }

    public float getEngineSpeed() {
        return entityData.get(ENGINE_SPEED);
    }

    public void setEngineSpeed(float speed) {
        entityData.set(ENGINE_SPEED, speed);
    }

    public int getStartTime() {
        return entityData.get(START_TIME);
    }

    public void setStartTime(int startTime) {
        entityData.set(START_TIME, startTime);
    }

}
