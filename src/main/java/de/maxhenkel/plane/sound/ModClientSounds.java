package de.maxhenkel.plane.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.world.level.Level;

public class ModClientSounds {

    public static void playSoundLoop(AbstractTickableSoundInstance loop, Level world) {
        if (world.isClientSide) {
            Minecraft.getInstance().getSoundManager().play(loop);
        }
    }

    public static void playSoundLoopDelayed(AbstractTickableSoundInstance loop, Level world, int delay) {
        if (world.isClientSide) {
            Minecraft.getInstance().getSoundManager().playDelayed(loop, delay);
        }
    }

}
