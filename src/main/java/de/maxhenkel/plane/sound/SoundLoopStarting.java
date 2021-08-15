package de.maxhenkel.plane.sound;

import de.maxhenkel.plane.entity.EntityPlaneSoundBase;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class SoundLoopStarting extends SoundLoopPlane {

    public SoundLoopStarting(EntityPlaneSoundBase plane, SoundEvent event, SoundSource category) {
        super(plane, event, category);
    }

    @Override
    public boolean shouldStopSound() {
        return plane.getStartTime() <= 0 || plane.isStarted();

    }

}
