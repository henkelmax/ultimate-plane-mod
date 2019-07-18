package de.maxhenkel.plane.sound;

import de.maxhenkel.plane.entity.EntityPlane;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class SoundLoopStarting extends SoundLoopPlane {

    public SoundLoopStarting(World world, EntityPlane plane, SoundEvent event, SoundCategory category) {
        super(world, plane, event, category);
        this.repeat = true;
    }

    @Override
    public void tick() {
        // pitch=plane.getBatterySoundPitchLevel();
        super.tick();
    }

    @Override
    public boolean shouldStopSound() {
        return plane.getStartTime() <= 0 || plane.isStarted();

    }

}
