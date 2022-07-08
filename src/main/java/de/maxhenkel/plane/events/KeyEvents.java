package de.maxhenkel.plane.events;

import de.maxhenkel.plane.Main;
import de.maxhenkel.plane.entity.EntityPlaneSoundBase;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
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
    public void onKeyInput(InputEvent.Key event) {
        Minecraft minecraft = Minecraft.getInstance();

        Player player = minecraft.player;

        if (player == null) {
            return;
        }

        Entity riding = player.getVehicle();

        if (!(riding instanceof EntityPlaneSoundBase)) {
            return;
        }

        EntityPlaneSoundBase plane = (EntityPlaneSoundBase) riding;

        if (player.equals(plane.getDriver())) {
            plane.updateControls(Main.UP_KEY.isDown(), Main.DOWN_KEY.isDown(), Main.FORWARD_KEY.isDown(), Main.BACK_KEY.isDown(), Main.LEFT_KEY.isDown(), Main.RIGHT_KEY.isDown(), Main.BRAKE_KEY.isDown(), Main.START_KEY.isDown());
        }

        if (Main.PLANE_KEY.consumeClick()) {
            if ((event.getModifiers() & GLFW.GLFW_MOD_CONTROL) == GLFW.GLFW_MOD_CONTROL) {
                Main.CLIENT_CONFIG.showPlaneInfo.set(!Main.CLIENT_CONFIG.showPlaneInfo.get());
                Main.CLIENT_CONFIG.showPlaneInfo.save();
            } else {
                plane.openGUI(player, false);
            }
        }
    }

}
