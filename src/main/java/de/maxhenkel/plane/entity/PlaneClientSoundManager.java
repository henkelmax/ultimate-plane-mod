package de.maxhenkel.plane.entity;

import de.maxhenkel.plane.sound.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;

public class PlaneClientSoundManager {

    private final EntityPlaneSoundBase plane;

    private SoundLoopStart startLoop;
    private SoundLoopIdle idleLoop;
    private SoundLoopHigh highLoop;
    private SoundLoopStarting startingLoop;
    private SoundLoopStop stopLoop;

    public PlaneClientSoundManager(EntityPlaneSoundBase plane) {
        this.plane = plane;
    }

    private int ticksSinceStarted = 0;

    public void updateSounds() {
        if (plane.isStarted()) {
            if (ticksSinceStarted >= 20) {
                if (plane.getEngineSpeed() <= 0F) {
                    checkIdleLoop();
                } else {
                    checkHighLoop();
                }
            } else {
                ticksSinceStarted++;
            }
        } else {
            ticksSinceStarted = 0;
        }
        if (plane.getStartTime() > 0) {
            checkStartingLoop();
        }
    }

    public void setStarted(boolean started, boolean playSound) {
        if (plane.level().isClientSide() && playSound) {
            if (!started) {
                checkStopLoop();
            } else {
                checkStartLoop();
            }
        }
    }

    public void checkIdleLoop() {
        if (!isSoundPlaying(idleLoop)) {
            idleLoop = new SoundLoopIdle(plane, ModSounds.ENGINE_IDLE.get(), SoundSource.NEUTRAL);
            ModClientSounds.playSoundLoop(idleLoop, plane.level());
        }
    }

    public void checkHighLoop() {
        if (!isSoundPlaying(highLoop)) {
            highLoop = new SoundLoopHigh(plane, ModSounds.ENGINE_HIGH.get(), SoundSource.NEUTRAL);
            ModClientSounds.playSoundLoop(highLoop, plane.level());
        }
    }

    public void checkStartLoop() {
        if (!isSoundPlaying(startLoop)) {
            startLoop = new SoundLoopStart(plane, ModSounds.ENGINE_START.get(), SoundSource.NEUTRAL);
            ModClientSounds.playSoundLoop(startLoop, plane.level());
        }
    }

    public void checkStopLoop() {
        if (!isSoundPlaying(stopLoop)) {
            stopLoop = new SoundLoopStop(plane, ModSounds.ENGINE_STOP.get(), SoundSource.NEUTRAL);
            ModClientSounds.playSoundLoop(stopLoop, plane.level());
        }
    }

    public void checkStartingLoop() {
        if (!isSoundPlaying(startingLoop)) {
            startingLoop = new SoundLoopStarting(plane, ModSounds.ENGINE_STARTING.get(), SoundSource.NEUTRAL);
            ModClientSounds.playSoundLoop(startingLoop, plane.level());
        }
    }

    public boolean isSoundPlaying(SoundInstance sound) {
        if (sound == null) {
            return false;
        }
        return Minecraft.getInstance().getSoundManager().isActive(sound);
    }

}
