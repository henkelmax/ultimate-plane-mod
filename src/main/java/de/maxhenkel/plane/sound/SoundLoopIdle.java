package de.maxhenkel.plane.sound;

import de.maxhenkel.plane.entity.EntityPlane;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class SoundLoopIdle extends SoundLoopPlane {

    private float volumeToReach;

    public SoundLoopIdle(World world, EntityPlane plane, SoundEvent event, SoundCategory category) {
        super(world, plane, event, category);
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
