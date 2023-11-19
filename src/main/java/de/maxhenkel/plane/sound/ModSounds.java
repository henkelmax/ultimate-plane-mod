package de.maxhenkel.plane.sound;

import de.maxhenkel.plane.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSounds {

    private static final DeferredRegister<SoundEvent> SOUND_REGISTER = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, Main.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> ENGINE_STOP = addSound("engine_stop");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENGINE_STARTING = addSound("engine_starting");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENGINE_START = addSound("engine_start");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENGINE_IDLE = addSound("engine_idle");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENGINE_HIGH = addSound("engine_high");
    public static final DeferredHolder<SoundEvent, SoundEvent> CRASH = addSound("crash");
    public static final DeferredHolder<SoundEvent, SoundEvent> RATCHET = addSound("ratchet");

    public static DeferredHolder<SoundEvent, SoundEvent> addSound(String soundName) {
        return SOUND_REGISTER.register(soundName, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Main.MODID, soundName)));
    }

    public static void init() {
        SOUND_REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static void playSound(SoundEvent evt, Level world, BlockPos pos, Player entity, SoundSource category, float volume) {
        playSound(evt, world, pos, entity, category, volume, 1.0F);
    }

    public static void playSound(SoundEvent evt, Level world, BlockPos pos, Player entity, SoundSource category, float volume, float pitch) {
        if (entity != null) {
            world.playSound(entity, pos, evt, category, volume, pitch);
        } else {
            if (!world.isClientSide) {
                world.playSound(null, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, evt, category, volume, pitch);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void playSoundLoop(AbstractTickableSoundInstance loop, Level world) {
        if (world.isClientSide) {
            Minecraft.getInstance().getSoundManager().play(loop);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void playSoundLoopDelayed(AbstractTickableSoundInstance loop, Level world, int delay) {
        if (world.isClientSide) {
            Minecraft.getInstance().getSoundManager().playDelayed(loop, delay);
        }
    }

}
