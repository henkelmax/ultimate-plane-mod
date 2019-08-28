package de.maxhenkel.plane.sound;

import de.maxhenkel.plane.entity.EntityPlane;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public abstract class SoundLoopPlane extends TickableSound {

    protected EntityPlane plane;

    public SoundLoopPlane(EntityPlane plane, SoundEvent event, SoundCategory category) {
        super(event, category);
        this.plane = plane;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 1F;
        this.pitch = 1F;
        this.priority = true;
        this.global = false;
        this.attenuationType = AttenuationType.LINEAR;
        this.updatePos();
    }

    public void updatePos() {
        this.x = (float) plane.posX;
        this.y = (float) plane.posY;
        this.z = (float) plane.posZ;
    }

    @Override
    public void tick() {
        if (donePlaying) {
            return;
        }

        if (!plane.isAlive()) {
            this.donePlaying = true;
            this.repeat = false;
            return;
        }

        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player == null || !player.isAlive()) {
            this.donePlaying = true;
            this.repeat = false;
            return;
        }

        if (shouldStopSound()) {
            this.donePlaying = true;
            this.repeat = false;
            return;
        }

        updatePos();
    }

    public abstract boolean shouldStopSound();
}
