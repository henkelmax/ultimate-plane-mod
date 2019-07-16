package de.maxhenkel.plane.sound;

import de.maxhenkel.plane.entity.EntityPlane;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class SoundLoopStop extends SoundLoopPlane {

    public SoundLoopStop(World world, EntityPlane plane, SoundEvent event, SoundCategory category) {
        super(world, plane, event, category);
        this.repeat = false;
    }

    @Override
    public boolean shouldStopSound() {
        return false;
    }
}
