package de.maxhenkel.plane.sound;

import de.maxhenkel.plane.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {

    private static final DeferredRegister<SoundEvent> SOUND_REGISTER = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Main.MODID);

    public static final RegistryObject<SoundEvent> ENGINE_STOP = addSound("engine_stop");
    public static final RegistryObject<SoundEvent> ENGINE_STARTING = addSound("engine_starting");
    public static final RegistryObject<SoundEvent> ENGINE_START = addSound("engine_start");
    public static final RegistryObject<SoundEvent> ENGINE_IDLE = addSound("engine_idle");
    public static final RegistryObject<SoundEvent> ENGINE_HIGH = addSound("engine_high");
    public static final RegistryObject<SoundEvent> CRASH = addSound("crash");
    public static final RegistryObject<SoundEvent> RATCHET = addSound("ratchet");

    public static RegistryObject<SoundEvent> addSound(String soundName) {
        return SOUND_REGISTER.register(soundName, () -> new SoundEvent(new ResourceLocation(Main.MODID, soundName)));
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
