package de.maxhenkel.plane.sound;

import de.maxhenkel.plane.entity.EntityPlane;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class SoundLoopHigh extends SoundLoopPlane {

    public SoundLoopHigh(World world, EntityPlane plane, SoundEvent event, SoundCategory category) {
        super(world, plane, event, category);
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
