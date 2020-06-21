package de.maxhenkel.plane.sound;

import de.maxhenkel.plane.entity.EntityPlaneSoundBase;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public class SoundLoopHigh extends SoundLoopPlane {

    public SoundLoopHigh(EntityPlaneSoundBase plane, SoundEvent event, SoundCategory category) {
        super(plane, event, category);
    }

    @Override
    public void tick() {
        pitch = 1F + plane.getEngineSpeed() / 2F;
        super.tick();
    }

    @Override
    public boolean shouldStopSound() {
        if (plane.getEngineSpeed() <= 0F) {
            return true;
        } else if (!plane.isStarted()) {
            return true;
        }

        return false;
    }

}
