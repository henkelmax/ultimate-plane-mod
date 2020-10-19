package de.maxhenkel.plane.sound;

import de.maxhenkel.plane.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ModSounds {
    public static SoundEvent ENGINE_STOP = registerSound("engine_stop");
    public static SoundEvent ENGINE_STARTING = registerSound("engine_starting");
    public static SoundEvent ENGINE_START = registerSound("engine_start");
    public static SoundEvent ENGINE_IDLE = registerSound("engine_idle");
    public static SoundEvent ENGINE_HIGH = registerSound("engine_high");
    public static SoundEvent CRASH = registerSound("crash");
    public static SoundEvent RATCHET = registerSound("ratchet");

    public static List<SoundEvent> getAll() {
        List<SoundEvent> sounds = new ArrayList<>();
        for (Field field : ModSounds.class.getFields()) {
            try {
                Object obj = field.get(null);
                if (obj instanceof SoundEvent) {
                    sounds.add((SoundEvent) obj);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return sounds;
    }

    public static SoundEvent registerSound(String soundName) {
        SoundEvent event = new SoundEvent(new ResourceLocation(Main.MODID, soundName));
        event.setRegistryName(new ResourceLocation(Main.MODID, soundName));
        return event;
    }

    public static void playSound(SoundEvent evt, World world, BlockPos pos, PlayerEntity entity, SoundCategory category, float volume) {
        playSound(evt, world, pos, entity, category, volume, 1.0F);
    }

    public static void playSound(SoundEvent evt, World world, BlockPos pos, PlayerEntity entity, SoundCategory category, float volume, float pitch) {
        if (entity != null) {
            world.playSound(entity, pos, evt, category, volume, pitch);
        } else {
            if (!world.isRemote) {
                world.playSound(null, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, evt, category, volume, pitch);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void playSoundLoop(TickableSound loop, World world) {
        if (world.isRemote) {
            Minecraft.getInstance().getSoundHandler().play(loop);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void playSoundLoopDelayed(TickableSound loop, World world, int delay) {
        if (world.isRemote) {
            Minecraft.getInstance().getSoundHandler().playDelayed(loop, delay);
        }
    }

}
