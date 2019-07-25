package de.maxhenkel.plane.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public class EntityPlaneWheelBase extends EntityPlaneHitboxBase {

    private int ticksSinceLiftOff;
    private float wheelRotation;
    private static final float WHEEL_ROTATION = 240F;

    public EntityPlaneWheelBase(EntityType type, World world) {
        super(type, world);
    }

    @Override
    public void tick() {
        super.tick();

        updateWheelRotation();
    }

    public void updateWheelRotation() {
        if (!isCollidedVertical()) {
            ticksSinceLiftOff++;
        } else if (ticksSinceLiftOff > 0) {
            ticksSinceLiftOff = 0;
        }

        wheelRotation += getWheelRotationAmount();
    }

    public float getWheelRotation(float partialTicks) {
        return wheelRotation + getWheelRotationAmount() * partialTicks;
    }

    public float getWheelRotationAmount() {
        float amount = WHEEL_ROTATION * (float) getMotion().length();
        if (!isCollidedVertical()) {
            amount = Math.max(amount - ticksSinceLiftOff, 0F);
        }
        return amount;
    }
}
