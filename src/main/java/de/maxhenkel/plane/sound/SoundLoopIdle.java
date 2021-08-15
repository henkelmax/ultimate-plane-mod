package de.maxhenkel.plane.sound;

import de.maxhenkel.plane.entity.EntityPlaneSoundBase;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class SoundLoopIdle extends SoundLoopPlane {

    private float volumeToReach;

    public SoundLoopIdle(EntityPlaneSoundBase plane, SoundEvent event, SoundSource category) {
        super(plane, event, category);
        volumeToReach = volume;
        volume = volume / 2.5F;
    }

    @Override
    public void tick() {
        if (volume < volumeToReach) {
            volume = Math.min(volume + volumeToReach / 2.5F, volumeToReach);
        }
        super.tick();
    }

    @Override
    public boolean shouldStopSound() {
        if (plane.getEngineSpeed() > 0F) {
            return true;
        } else if (!plane.isStarted()) {
            return true;
        }
        return false;
    }

}
