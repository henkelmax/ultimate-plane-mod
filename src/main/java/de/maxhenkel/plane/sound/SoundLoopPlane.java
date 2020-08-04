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
        this.x = (float) plane.getPosX();
        this.y = (float) plane.getPosY();
        this.z = (float) plane.getPosZ();
    }

    @Override
    public void tick() {
        if (isDonePlaying()) {
            return;
        }

        if (!plane.isAlive()) {
            func_239509_o_();
            return;
        }

        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player == null || !player.isAlive()) {
            func_239509_o_();
            return;
        }

        if (shouldStopSound()) {
            func_239509_o_();
            return;
        }

        updatePos();
    }

    public abstract boolean shouldStopSound();

}
