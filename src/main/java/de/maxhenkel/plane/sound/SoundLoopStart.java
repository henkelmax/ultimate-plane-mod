package de.maxhenkel.plane.sound;

import de.maxhenkel.plane.entity.EntityPlaneSoundBase;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class SoundLoopStart extends SoundLoopPlane {

    public SoundLoopStart(EntityPlaneSoundBase plane, SoundEvent event, SoundSource category) {
        super(plane, event, category);
        this.looping = false;
    }

    @Override
    public boolean shouldStopSound() {
        return !plane.isStarted();
    }
}
