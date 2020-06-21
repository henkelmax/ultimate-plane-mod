package de.maxhenkel.plane.sound;

import de.maxhenkel.plane.entity.EntityPlaneSoundBase;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public class SoundLoopStart extends SoundLoopPlane {

    public SoundLoopStart(EntityPlaneSoundBase plane, SoundEvent event, SoundCategory category) {
        super(plane, event, category);
        this.repeat = false;
    }

    @Override
    public boolean shouldStopSound() {
        return !plane.isStarted();
    }
}
