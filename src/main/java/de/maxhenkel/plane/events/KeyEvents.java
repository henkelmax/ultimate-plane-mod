package de.maxhenkel.plane.events;

import de.maxhenkel.plane.Main;
import de.maxhenkel.plane.entity.EntityPlaneSoundBase;
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

        if (!(riding instanceof EntityPlaneSoundBase)) {
            return;
        }

        EntityPlaneSoundBase plane = (EntityPlaneSoundBase) riding;

        if (player.equals(plane.getDriver())) {
            plane.updateControls(Main.UP_KEY.isKeyDown(), Main.DOWN_KEY.isKeyDown(), Main.FORWARD_KEY.isKeyDown(), Main.BACK_KEY.isKeyDown(), Main.LEFT_KEY.isKeyDown(), Main.RIGHT_KEY.isKeyDown(), Main.BRAKE_KEY.isKeyDown(), Main.START_KEY.isKeyDown());
        }

        if (Main.PLANE_KEY.isPressed()) {
            if ((event.getModifiers() & GLFW.GLFW_MOD_CONTROL) == GLFW.GLFW_MOD_CONTROL) {
                Main.CLIENT_CONFIG.showPlaneInfo.set(!Main.CLIENT_CONFIG.showPlaneInfo.get());
                Main.CLIENT_CONFIG.showPlaneInfo.save();
            } else {
                plane.openGUI(player, false);
            }
        }
    }

}
