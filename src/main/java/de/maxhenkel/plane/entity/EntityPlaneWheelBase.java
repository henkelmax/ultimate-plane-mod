package de.maxhenkel.plane.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public abstract class EntityPlaneWheelBase extends EntityPlaneInventoryBase {

    private int ticksSinceLiftOff;
    private int ticksSinceEngineOff;
    private float wheelRotation;
    private float propellerRotation;
    private static final float WHEEL_ROTATION = 240F;
    private static final float PROPELLER_ROTATION = 240F;

    public EntityPlaneWheelBase(EntityType type, Level world) {
        super(type, world);

        ticksSinceEngineOff = 100000;
    }

    @Override
    public void tick() {
        super.tick();

        updateRotation();
    }

    public void updateRotation() {
        if (!isCollidedVertical()) {
            ticksSinceLiftOff++;
        } else if (ticksSinceLiftOff > 0) {
            ticksSinceLiftOff = 0;
        }

        if (!isStarted() && getStartTime() <= 0) {
            ticksSinceEngineOff++;
        } else if (ticksSinceEngineOff > 0) {
            ticksSinceEngineOff = 0;
        }

        wheelRotation += getWheelRotationAmount();

        propellerRotation += getPropellerRotationAmount();
    }

    public float getWheelRotation(float partialTicks) {
        return wheelRotation + getWheelRotationAmount() * partialTicks;
    }

    public float getPropellerRotation(float partialTicks) {
        return propellerRotation + getPropellerRotationAmount() * partialTicks;
    }

    public float getWheelRotationAmount() {
        float amount = WHEEL_ROTATION * (float) getDeltaMovement().length();
        if (!isCollidedVertical()) {
            amount = Math.max(amount - ticksSinceLiftOff, 0F);
        }
        return amount;
    }

    public float getPropellerRotationAmount() {
        float amount = PROPELLER_ROTATION * (getEngineSpeed() + 0.35F);
        if (!isStarted() && getStartTime() <= 0) {
            amount = Math.max(amount - ticksSinceEngineOff, 0F);
        }
        return amount;
    }

}
