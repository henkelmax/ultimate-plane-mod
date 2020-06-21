package de.maxhenkel.plane.sound;

import de.maxhenkel.plane.entity.EntityPlaneSoundBase;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public class SoundLoopStarting extends SoundLoopPlane {

    public SoundLoopStarting(EntityPlaneSoundBase plane, SoundEvent event, SoundCategory category) {
        super(plane, event, category);
    }

    @Override
    public boolean shouldStopSound() {
        return plane.getStartTime() <= 0 || plane.isStarted();

    }

}
