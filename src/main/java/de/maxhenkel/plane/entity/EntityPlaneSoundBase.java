package de.maxhenkel.plane.entity;

import de.maxhenkel.plane.sound.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class EntityPlaneSoundBase extends EntityPlaneWheelBase {

    @OnlyIn(Dist.CLIENT)
    private SoundLoopStart startLoop;
    @OnlyIn(Dist.CLIENT)
    private SoundLoopIdle idleLoop;
    @OnlyIn(Dist.CLIENT)
    private SoundLoopHigh highLoop;
    @OnlyIn(Dist.CLIENT)
    private SoundLoopStarting startingLoop;
    @OnlyIn(Dist.CLIENT)
    private SoundLoopStop stopLoop;

    public EntityPlaneSoundBase(EntityType type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide) {
            updateSounds();
        }
    }

    private int ticksSinceStarted = 0;

    private void updateSounds() {
        if (isStarted()) {

            if (ticksSinceStarted >= 20) {
                if (getEngineSpeed() <= 0F) {
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
        if (getStartTime() > 0) {
            checkStartingLoop();
        }
    }

    @Override
    public void setStarted(boolean started) {
        setStarted(started, true);
    }

    public void setStarted(boolean started, boolean playSound) {
        super.setStarted(started);
        if (level().isClientSide && playSound) {
            if (!started) {
                checkStopLoop();
            } else {
                checkStartLoop();
            }
        }
    }

    @Override
    public void damagePlane(double damage, boolean horizontal) {
        super.damagePlane(damage, horizontal);

        ModSounds.playSound(ModSounds.CRASH.get(), level(), blockPosition(), null, SoundSource.NEUTRAL, 1.0F);
    }

    @OnlyIn(Dist.CLIENT)
    public void checkIdleLoop() {
        if (!isSoundPlaying(idleLoop)) {
            idleLoop = new SoundLoopIdle(this, ModSounds.ENGINE_IDLE.get(), SoundSource.NEUTRAL);
            ModSounds.playSoundLoop(idleLoop, level());
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void checkHighLoop() {
        if (!isSoundPlaying(highLoop)) {
            highLoop = new SoundLoopHigh(this, ModSounds.ENGINE_HIGH.get(), SoundSource.NEUTRAL);
            ModSounds.playSoundLoop(highLoop, level());
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void checkStartLoop() {
        if (!isSoundPlaying(startLoop)) {
            startLoop = new SoundLoopStart(this, ModSounds.ENGINE_START.get(), SoundSource.NEUTRAL);
            ModSounds.playSoundLoop(startLoop, level());
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void checkStopLoop() {
        if (!isSoundPlaying(stopLoop)) {
            stopLoop = new SoundLoopStop(this, ModSounds.ENGINE_STOP.get(), SoundSource.NEUTRAL);
            ModSounds.playSoundLoop(stopLoop, level());
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void checkStartingLoop() {
        if (!isSoundPlaying(startingLoop)) {
            startingLoop = new SoundLoopStarting(this, ModSounds.ENGINE_STARTING.get(), SoundSource.NEUTRAL);
            ModSounds.playSoundLoop(startingLoop, level());
        }
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isSoundPlaying(SoundInstance sound) {
        if (sound == null) {
            return false;
        }
        return Minecraft.getInstance().getSoundManager().isActive(sound);
    }

}
