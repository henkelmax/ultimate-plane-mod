package de.maxhenkel.plane.entity;

import de.maxhenkel.plane.Main;
import de.maxhenkel.plane.net.MessageControlPlane;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class EntityPlaneControlBase extends EntityPlaneDamageBase {

    private static final DataParameter<Float> ENGINE_SPEED = EntityDataManager.createKey(EntityPlaneControlBase.class,
            DataSerializers.FLOAT);
    private static final DataParameter<Boolean> STARTED = EntityDataManager.createKey(EntityPlaneControlBase.class,
            DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> START_TIME = EntityDataManager.createKey(EntityPlaneControlBase.class,
            DataSerializers.VARINT);
    private static final DataParameter<Boolean> THRUST_POSITIVE = EntityDataManager.createKey(EntityPlaneControlBase.class,
            DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> THRUST_NEGATIVE = EntityDataManager.createKey(EntityPlaneControlBase.class,
            DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> LEFT = EntityDataManager.createKey(EntityPlaneControlBase.class,
            DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> RIGHT = EntityDataManager.createKey(EntityPlaneControlBase.class,
            DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> UP = EntityDataManager.createKey(EntityPlaneControlBase.class,
            DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> DOWN = EntityDataManager.createKey(EntityPlaneControlBase.class,
            DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> BRAKE = EntityDataManager.createKey(EntityPlaneControlBase.class,
            DataSerializers.BOOLEAN);

    public static final double MAX_ENGINE_SPEED = 1.5D;
    public static final double ENGINE_ACCELERATION = 0.005D;
    public static final double MIN_TAKEOFF_SPEED = 1.3D;
    public static final double BRAKE_POWER = 0.012D;

    public EntityPlaneControlBase(EntityType type, World worldIn) {
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

        if (!world.isRemote) {
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

        double speed = getMotion().length();

        float rotationSpeed = 0;
        if (Math.abs(speed) > 0F) {
            rotationSpeed = MathHelper.abs(0.5F / (float) Math.pow(speed, 2)); //rotation modifier+0.5
            rotationSpeed = MathHelper.clamp(rotationSpeed, 1.0F, 5.0F);
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
        rotationYaw += deltaRotation;
        float delta = Math.abs(rotationYaw - prevRotationYaw);
        while (rotationYaw > 180F) {
            rotationYaw -= 360F;
            prevRotationYaw = rotationYaw - delta;
        }
        while (rotationYaw <= -180F) {
            rotationYaw += 360F;
            prevRotationYaw = delta + rotationYaw;
        }
        // ----- YAW ------

        // ----- PITCH ------
        if (isUp()) {
            rotationPitch -= 1F;
        } else if (isDown()) {
            rotationPitch += 1F;
        }

        rotationPitch = Math.max(rotationPitch, -90F);
        rotationPitch = Math.min(rotationPitch, 90F);

        float groundPitchTolerance = 7F;

        if (isCollidedVertical()) {
            if (rotationPitch > 0) {
                rotationPitch -= 10F;
                if (rotationPitch < 0) {
                    rotationPitch = 0;
                }
            }

            if (rotationPitch < -groundPitchTolerance && speed < MIN_TAKEOFF_SPEED) {
                rotationPitch += 10F;
                if (rotationPitch > -groundPitchTolerance) {
                    rotationPitch = -groundPitchTolerance;
                }
            }
        }
        // ----- PITCH ------
    }

    private void controlPlane() {

        if (!isBeingRidden()) {
            setThrustPositive(false);
            setThrustNegative(false);
            setLeft(false);
            setRight(false);
            setUp(false);
            setDown(false);
            setBrake(true);
            setStartTime(0);
        }

        Vec3d motionVector = getMotion();
        double verticalMotion = Math.abs(motionVector.y);
        double horizontalMotion = getHorizontalMotion(motionVector);
        float engineSpeed = getEngineSpeed();

        if (isCollidedVertical()) {
            double speed = getMotion().length();
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

            Vec3d motion = getLookVec().normalize().scale(speed).mul(1D, 0D, 1D);
            if (speed < MIN_TAKEOFF_SPEED) {
                motion.add(0D, -0.1D, 0D);
            }

            setMotion(motion);
            if (speed > 0D) {
                move(MoverType.SELF, getMotion());
            }
        } else {
            double fallSpeed = 0.08D;
            Vec3d lookVec = getLookVec();
            float modifiedPitch = (rotationPitch < 0F ? rotationPitch : Math.min(rotationPitch * 1.5F, 90F)) - 5F;
            float pitch = modifiedPitch * ((float) Math.PI / 180F);
            double horizontalLook = Math.sqrt(lookVec.x * lookVec.x + lookVec.z * lookVec.z);

            double lookLength = lookVec.length();
            float cosPitch = MathHelper.cos(pitch);
            cosPitch = (float) ((double) cosPitch * (double) cosPitch * Math.min(1.0D, lookLength / 0.4D));
            motionVector = getMotion().add(0.0D, fallSpeed * (-1.0D + (double) cosPitch * 0.75D), 0.0D);
            if (motionVector.y < 0.0D && horizontalLook > 0.0D) {
                double down = motionVector.y * -0.1D * (double) cosPitch;
                motionVector = motionVector.add(lookVec.x * down / horizontalLook, down, lookVec.z * down / horizontalLook);
            }

            if (pitch < 0.0F && horizontalLook > 0.0D) {
                double d13 = horizontalMotion * (double) (-MathHelper.sin(pitch)) * 0.04D;
                motionVector = motionVector.add(-lookVec.x * d13 / horizontalLook, d13 * 3.2D, -lookVec.z * d13 / horizontalLook);
            }

            if (horizontalLook > 0.0D) {
                motionVector = motionVector.add((lookVec.x / horizontalLook * horizontalMotion - motionVector.x) * 0.1D, 0.0D, (lookVec.z / horizontalLook * horizontalMotion - motionVector.z) * 0.1D);
            }

            motionVector = motionVector.mul(0.99D, 0.98D, 0.99D);

            double speed = motionVector.length();

            if (speed < MAX_ENGINE_SPEED * engineSpeed) {
                double addSpeed = 0D;
                addSpeed = addSpeed + engineSpeed * ENGINE_ACCELERATION * 4F;//0.04D;
                if (speed + addSpeed > MAX_ENGINE_SPEED * engineSpeed) {
                    addSpeed = (MAX_ENGINE_SPEED * engineSpeed) - speed;
                }
                if (addSpeed < 0D) {
                    addSpeed = 0D;
                }

                if (getPitchPercentage() < -0.25F) {
                    addSpeed = 0D;
                }

                Vec3d addVec = getLookVec().normalize().scale(addSpeed);

                motionVector = motionVector.add(new Vec3d(addVec.x, 0D/*Math.min(addVec.y, 0.0001D)*/, addVec.z));
            }

            setMotion(motionVector);

            move(MoverType.SELF, getMotion());
        }

        if (world.isRemote) {
            return;
        }
        if (isCollidedHorizontal()) {
            double newHorizontalMotion = getHorizontalMotion(getMotion());
            double motionDifference = horizontalMotion - newHorizontalMotion;
            double damage = motionDifference * 100D - 12D;
            if (damage > 0D) {
                damagePlane(damage, true);
                System.out.println("dmg: " + damage);
            }
        }

        if (isCollidedVertical()) {
            double newVerticalMotion = Math.abs(getMotion().y);
            double motionDifference = verticalMotion - newVerticalMotion;
            double damage = motionDifference * 100D - 10D;
            if (damage > 0D) {
                damagePlane(damage, false);
                System.out.println("dmg vert: " + damage);
            }
        }
    }

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
        onGroundLast = onGround;
        return last || last2 || onGround;
    }

    public boolean isCollidedHorizontal() {
        return collidedHorizontally;
    }

    public double getHorizontalMotion(Vec3d vec3d) {
        return Math.sqrt(vec3d.x * vec3d.x + vec3d.z * vec3d.z);
    }

    public double getAngle(Vec3d vec1, Vec3d vec2) {
        return Math.acos(Math.abs(vec1.dotProduct(vec2)) / (vec1.length() * vec2.length()));
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
        return rotationPitch / 90F;
    }

    @Override
    protected void registerData() {
        super.registerData();
        dataManager.register(STARTED, false);
        dataManager.register(START_TIME, 0);
        dataManager.register(ENGINE_SPEED, 0F);
        dataManager.register(THRUST_POSITIVE, false);
        dataManager.register(THRUST_NEGATIVE, false);
        dataManager.register(LEFT, false);
        dataManager.register(RIGHT, false);
        dataManager.register(UP, false);
        dataManager.register(DOWN, false);
        dataManager.register(BRAKE, false);
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

        if (world.isRemote && needsUpdate) {
            Main.SIMPLE_CHANNEL.sendToServer(new MessageControlPlane(up, down, thrustPos, thrustNeg, left, right, braking, starting));
        }
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putFloat("EngineSpeed", getEngineSpeed());
        compound.putBoolean("Started", isStarted());
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        setEngineSpeed(compound.getFloat("EngineSpeed"));
        ((EntityPlaneSoundBase) this).setStarted(compound.getBoolean("Started"), false);
    }

    public boolean isStarted() {
        return dataManager.get(STARTED);
    }

    public void setStarted(boolean started) {
        dataManager.set(STARTED, started);
    }

    public boolean isBrake() {
        return dataManager.get(BRAKE);
    }

    public void setBrake(boolean breaking) {
        dataManager.set(BRAKE, breaking);
    }

    public boolean isThrustPositive() {
        return dataManager.get(THRUST_POSITIVE);
    }

    public void setThrustPositive(boolean thrustPositive) {
        dataManager.set(THRUST_POSITIVE, thrustPositive);
    }

    public boolean isThrustNegative() {
        return dataManager.get(THRUST_NEGATIVE);
    }

    public void setThrustNegative(boolean thrustNegative) {
        dataManager.set(THRUST_NEGATIVE, thrustNegative);
    }

    public boolean isLeft() {
        return dataManager.get(LEFT);
    }

    public void setLeft(boolean left) {
        dataManager.set(LEFT, left);
    }

    public boolean isRight() {
        return dataManager.get(RIGHT);
    }

    public void setRight(boolean right) {
        dataManager.set(RIGHT, right);
    }

    public boolean isUp() {
        return dataManager.get(UP);
    }

    public void setUp(boolean up) {
        dataManager.set(UP, up);
    }

    public boolean isDown() {
        return dataManager.get(DOWN);
    }

    public void setDown(boolean down) {
        dataManager.set(DOWN, down);
    }

    public float getEngineSpeed() {
        return dataManager.get(ENGINE_SPEED);
    }

    public void setEngineSpeed(float speed) {
        dataManager.set(ENGINE_SPEED, speed);
    }

    public int getStartTime() {
        return dataManager.get(START_TIME);
    }

    public void setStartTime(int startTime) {
        dataManager.set(START_TIME, startTime);
    }
}
