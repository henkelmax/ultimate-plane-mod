package de.maxhenkel.plane.sound;

import de.maxhenkel.plane.entity.EntityPlane;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public abstract class SoundLoopPlane extends TickableSound {

    protected World world;
    protected EntityPlane plane;

    public SoundLoopPlane(World world, EntityPlane car, SoundEvent event, SoundCategory category) {
        super(event, category);
        this.world = world;
        this.plane = car;
        this.repeat = true;
        this.repeatDelay = 0;
        this.updatePos();
        this.volume = 1F;
        this.pitch = 1F;
    }

    public void updatePos() {
        this.x = (float) plane.posX;
        this.y = (float) plane.posY;
        this.z = (float) plane.posZ;
    }

    @Override
    public void tick() {
        if (donePlaying) {
            onFinishPlaying();
            return;
        }

        if (!plane.isAlive()) {
            this.donePlaying = true;
            this.repeat = false;
            return;
        }

        if (world.isRemote) {
            ClientPlayerEntity player = Minecraft.getInstance().player;
            if (player == null || !player.isAlive()) {
                this.donePlaying = true;
                this.repeat = false;
                return;
            }
        }

        if (shouldStopSound()) {
            this.donePlaying = true;
            this.repeat = false;
            return;
        }

        updatePos();
    }

    public void onFinishPlaying() {

    }

    public void setDonePlaying() {
        donePlaying = true;
    }

    public abstract boolean shouldStopSound();
}
