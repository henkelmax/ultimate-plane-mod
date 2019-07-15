package de.maxhenkel.plane.entity;

import de.maxhenkel.plane.Main;
import de.maxhenkel.plane.net.MessageControlPlane;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class EntityPlaneControlBase extends EntityPlaneBase {

    private static final DataParameter<Float> ENGINE_SPEED = EntityDataManager.createKey(EntityPlaneControlBase.class,
            DataSerializers.FLOAT);
    private static final DataParameter<Boolean> STARTED = EntityDataManager.createKey(EntityPlaneControlBase.class,
            DataSerializers.BOOLEAN);
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
    private static final DataParameter<Boolean> BREAK = EntityDataManager.createKey(EntityPlaneControlBase.class,
            DataSerializers.BOOLEAN);

    public static final double MAX_ENGINE_SPEED = 1D;
    public static final double MIN_TAKEOFF_SPEED = 0.8D;

    public EntityPlaneControlBase(EntityType type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    public void tick() {
        super.tick();

        if (isThrustPositive()) {
            setEngineSpeed(Math.min(getEngineSpeed() + 0.025F, 1F));
        } else if (isThrustNegative()) {
            setEngineSpeed(Math.max(getEngineSpeed() - 0.025F, 0F));
        }

        controlPlane();
        handleRotation();
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

        if (onGroundEnhanced()) {
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
            setBreak(false);
        }

        Vec3d motionVector = getMotion();
        double verticalMotion = Math.abs(motionVector.y);
        double horizontalMotion = getHorizontalMotion(motionVector);

        if (onGroundEnhanced()) {
            Vec3d motion = getLookVec();

            double speed = getMotion().length();

            float engineSpeed = getEngineSpeed();

            if (speed < MAX_ENGINE_SPEED * engineSpeed) {
                speed = speed + engineSpeed * 0.01D;

                if (speed > MAX_ENGINE_SPEED * engineSpeed) {
                    speed = MAX_ENGINE_SPEED * engineSpeed;
                }
            }
            if (isBreak()) {
                speed = decreaseToZero(speed, 0.01F); // break resistance
            }

            if (engineSpeed <= 0F) {
                speed = decreaseToZero(speed, 0.0025F); // ground resistance
            }

            motion = motion.normalize().scale(speed);
            motion = new Vec3d(motion.x, 0D, motion.z);

            if (speed < MIN_TAKEOFF_SPEED) {
                motion.add(0D, -0.1D, 0D);
            }

            setMotion(motion);
            if (motion.length() > 0D) {
                move(MoverType.SELF, getMotion());
            }
        } else {
            double fallSpeed = 0.08D;
            Vec3d lookVec = getLookVec();
            float pitch = rotationPitch * ((float) Math.PI / 180F);
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

            double engineSpeed = getEngineSpeed();

            if (speed < MAX_ENGINE_SPEED * engineSpeed) {
                double addSpeed = 0D;
                addSpeed = addSpeed + engineSpeed * 0.04D;
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

        if (collidedHorizontally && !world.isRemote) {
            double newHorizontalMotion = getHorizontalMotion(getMotion());
            double motionDifference = horizontalMotion - newHorizontalMotion;
            double damage = motionDifference * 100D - 12D;
            if (damage > 0D) {
                System.out.println("dmg: " + damage);
                //this.playSound(this.getFallSound((int)damage), 1.0F, 1.0F);
                //this.attackEntityFrom(DamageSource.FLY_INTO_WALL, damage);
            }
        }

        if (onGroundEnhanced() && !world.isRemote) {
            double newVerticalMotion = Math.abs(getMotion().y);
            double motionDifference = verticalMotion - newVerticalMotion;
            double damage = motionDifference * 100D - 10D;
            if (damage > 0D) {
                System.out.println("dmg vert: " + damage);
                //this.playSound(this.getFallSound((int)damage), 1.0F, 1.0F);
                //this.attackEntityFrom(DamageSource.FLY_INTO_WALL, damage);
            }
        }
    }

    private boolean onGroundLast;
    private boolean onGroundLast2;

    public boolean onGroundEnhanced() {
        boolean last = onGroundLast;
        boolean last2 = onGroundLast2;
        onGroundLast2 = onGroundLast;
        onGroundLast = onGround;
        return last || last2 || onGround;
    }

    public double getHorizontalMotion(Vec3d vec3d) {
        return Math.sqrt(vec3d.x * vec3d.x + vec3d.z * vec3d.z);
    }

    public double getAngle(Vec3d vec1, Vec3d vec2) {
        return Math.acos(Math.abs(vec1.dotProduct(vec2)) / (vec1.length() * vec2.length()));
    }

    private static float decreaseToZero(float num, float amount) {
        float erg;
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
        dataManager.register(ENGINE_SPEED, 0F);
        dataManager.register(THRUST_POSITIVE, false);
        dataManager.register(THRUST_NEGATIVE, false);
        dataManager.register(LEFT, false);
        dataManager.register(RIGHT, false);
        dataManager.register(UP, false);
        dataManager.register(DOWN, false);
        dataManager.register(BREAK, false);
    }

    public void updateControls(boolean up, boolean down, boolean thrustPos, boolean thrustNeg, boolean left, boolean right, boolean breaking) {
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

        if (isBreak() != breaking) {
            setBreak(breaking);
            needsUpdate = true;
        }

        if (world.isRemote && needsUpdate) {
            Main.SIMPLE_CHANNEL.sendToServer(new MessageControlPlane(up, down, thrustPos, thrustNeg, left, right, breaking));
        }
    }

    public boolean isStarted() {
        return dataManager.get(STARTED);
    }

    public void setStarted(boolean started) {
        dataManager.set(STARTED, started);
    }

    public boolean isBreak() {
        return dataManager.get(BREAK);
    }

    public void setBreak(boolean breaking) {
        dataManager.set(BREAK, breaking);
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
}
