package de.maxhenkel.plane.sound;

import de.maxhenkel.plane.entity.EntityPlaneSoundBase;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public class SoundLoopStop extends SoundLoopPlane {

    public SoundLoopStop(EntityPlaneSoundBase plane, SoundEvent event, SoundCategory category) {
        super(plane, event, category);
        this.looping = false;
    }

    @Override
    public boolean shouldStopSound() {
        return false;
    }
}
