package de.maxhenkel.plane.sound;

import de.maxhenkel.plane.entity.EntityPlaneSoundBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public abstract class SoundLoopPlane extends AbstractTickableSoundInstance {

    protected EntityPlaneSoundBase plane;

    public SoundLoopPlane(EntityPlaneSoundBase plane, SoundEvent event, SoundSource category) {
        super(event, category, SoundInstance.createUnseededRandom());
        this.plane = plane;
        this.looping = true;
        this.delay = 0;
        this.volume = 1F;
        this.pitch = 1F;
        this.relative = false;
        this.attenuation = Attenuation.LINEAR;
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

        LocalPlayer player = Minecraft.getInstance().player;
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
