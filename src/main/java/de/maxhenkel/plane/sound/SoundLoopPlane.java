package de.maxhenkel.plane.sound;

import de.maxhenkel.plane.entity.EntityPlaneSoundBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public abstract class SoundLoopPlane extends TickableSound {

    protected EntityPlaneSoundBase plane;

    public SoundLoopPlane(EntityPlaneSoundBase plane, SoundEvent event, SoundCategory category) {
        super(event, category);
        this.plane = plane;
        this.looping = true;
        this.delay = 0;
        this.volume = 1F;
        this.pitch = 1F;
        this.priority = true;
        this.relative = false;
        this.attenuation = AttenuationType.LINEAR;
        this.updatePos();
    }

    public void updatePos() {
        this.x = (float) plane.getX();
        this.y = (float) plane.getY();
        this.z = (float) plane.getZ();
    }

    @Override
    public void tick() {
        if (isStopped()) {
            return;
        }

        if (!plane.isAlive()) {
            stop();
            return;
        }

        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player == null || !player.isAlive()) {
            stop();
            return;
        }

        if (shouldStopSound()) {
            stop();
            return;
        }

        updatePos();
    }

    public abstract boolean shouldStopSound();

}
