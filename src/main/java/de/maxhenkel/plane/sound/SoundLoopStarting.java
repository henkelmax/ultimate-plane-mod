package de.maxhenkel.plane.sound;

import de.maxhenkel.plane.entity.EntityPlane;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public class SoundLoopStarting extends SoundLoopPlane {

    public SoundLoopStarting(EntityPlane plane, SoundEvent event, SoundCategory category) {
        super(plane, event, category);
    }

    @Override
    public boolean shouldStopSound() {
        return plane.getStartTime() <= 0 || plane.isStarted();

    }

}
