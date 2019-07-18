package de.maxhenkel.plane.entity;

import de.maxhenkel.plane.sound.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.entity.EntityType;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class EntityPlaneSoundBase extends EntityPlaneControlBase {

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

    public EntityPlaneSoundBase(EntityType type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    public void tick() {
        super.tick();

        if (world.isRemote) {
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
        super.setStarted(started);
        if (world.isRemote) {
            if (!started) {
                checkStopLoop();
            } else {
                checkStartLoop();
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void checkIdleLoop() {
        if (!isSoundPlaying(idleLoop)) {
            idleLoop = new SoundLoopIdle(world, (EntityPlane) this, ModSounds.ENGINE_IDLE, SoundCategory.NEUTRAL);
            ModSounds.playSoundLoop(idleLoop, world);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void checkHighLoop() {
        if (!isSoundPlaying(highLoop)) {
            highLoop = new SoundLoopHigh(world, (EntityPlane) this, ModSounds.ENGINE_HIGH, SoundCategory.NEUTRAL);
            ModSounds.playSoundLoop(highLoop, world);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void checkStartLoop() {
        if (!isSoundPlaying(startLoop)) {
            startLoop = new SoundLoopStart(world, (EntityPlane) this, ModSounds.ENGINE_START, SoundCategory.NEUTRAL);
            ModSounds.playSoundLoop(startLoop, world);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void checkStopLoop() {
        if (!isSoundPlaying(stopLoop)) {
            stopLoop = new SoundLoopStop(world, (EntityPlane) this, ModSounds.ENGINE_STOP, SoundCategory.NEUTRAL);
            ModSounds.playSoundLoop(stopLoop, world);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void checkStartingLoop() {
        if (!isSoundPlaying(startingLoop)) {
            startingLoop = new SoundLoopStarting(world, (EntityPlane) this, ModSounds.ENGINE_STARTING, SoundCategory.NEUTRAL);
            ModSounds.playSoundLoop(startingLoop, world);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isSoundPlaying(ISound sound) {
        if (sound == null) {
            return false;
        }
        return Minecraft.getInstance().getSoundHandler().func_215294_c(sound);
    }
}
