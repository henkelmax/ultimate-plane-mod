package de.maxhenkel.plane.events;

import de.maxhenkel.plane.Config;
import de.maxhenkel.plane.entity.EntityPlane;
import de.maxhenkel.plane.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class KeyEvents {

    public KeyEvents() {

    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        Minecraft minecraft = Minecraft.getInstance();

        PlayerEntity player = minecraft.player;

        if (player == null) {
            return;
        }

        Entity riding = player.getRidingEntity();

        if (!(riding instanceof EntityPlane)) {
            return;
        }

        EntityPlane plane = (EntityPlane) riding;

        if (player.equals(plane.getDriver())) {
            plane.updateControls(Main.UP_KEY.isKeyDown(), Main.DOWN_KEY.isKeyDown(), Main.FORWARD_KEY.isKeyDown(), Main.BACK_KEY.isKeyDown(), Main.LEFT_KEY.isKeyDown(), Main.RIGHT_KEY.isKeyDown(), Main.BRAKE_KEY.isKeyDown(), Main.START_KEY.isKeyDown());
        }

        if (Main.PLANE_KEY.isPressed()) {
            if ((event.getModifiers() & GLFW.GLFW_MOD_CONTROL) == GLFW.GLFW_MOD_CONTROL) {
                Config.SHOW_PLANE_INFO.set(!Config.SHOW_PLANE_INFO.get());
                Config.SHOW_PLANE_INFO.save();
            } else {
                plane.openGUI(player);
            }
        }
    }

}
