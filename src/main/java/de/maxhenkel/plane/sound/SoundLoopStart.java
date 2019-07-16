package de.maxhenkel.plane.sound;

import de.maxhenkel.plane.entity.EntityPlane;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class SoundLoopStart extends SoundLoopPlane {

    public SoundLoopStart(World world, EntityPlane plane, SoundEvent event, SoundCategory category) {
        super(world, plane, event, category);
        this.repeat = false;
    }

    @Override
    public boolean shouldStopSound() {
        return !plane.isStarted();
    }
}
