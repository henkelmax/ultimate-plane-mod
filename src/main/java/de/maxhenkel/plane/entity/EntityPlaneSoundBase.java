package de.maxhenkel.plane.entity;

import de.maxhenkel.plane.sound.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public abstract class EntityPlaneSoundBase extends EntityPlaneWheelBase {

    private PlaneClientSoundManager soundManager;

    public EntityPlaneSoundBase(EntityType type, Level level) {
        super(type, level);
        if (level.isClientSide()) {
            soundManager = new PlaneClientSoundManager(this);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide()) {
            soundManager.updateSounds();
        }
    }

    @Override
    public void setStarted(boolean started) {
        super.setStarted(started);
        if (level().isClientSide()) {
            soundManager.setStarted(started, true);
        }
    }

    public void setStarted(boolean started, boolean playSound) {
        if (level().isClientSide()) {
            soundManager.setStarted(started, playSound);
        }
    }

    @Override
    public void damagePlane(float damage, boolean horizontal) {
        super.damagePlane(damage, horizontal);
        ModSounds.playSound(ModSounds.CRASH.get(), level(), blockPosition(), null, SoundSource.NEUTRAL, 1F);
    }

}
