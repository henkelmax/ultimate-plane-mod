package de.maxhenkel.plane.events;

import de.maxhenkel.plane.PlaneMod;
import de.maxhenkel.plane.PlaneClientMod;
import de.maxhenkel.plane.entity.EntityPlaneBase;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import org.lwjgl.glfw.GLFW;

public class KeyEvents {

    @SubscribeEvent
    public void onKeyInput(InputEvent.Key event) {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        if (!(player.getVehicle() instanceof EntityPlaneBase plane)) {
            return;
        }

        if (player.equals(plane.getDriver())) {
            plane.updateControls(PlaneClientMod.UP_KEY.isDown(), PlaneClientMod.DOWN_KEY.isDown(), PlaneClientMod.FORWARD_KEY.isDown(), PlaneClientMod.BACK_KEY.isDown(), PlaneClientMod.LEFT_KEY.isDown(), PlaneClientMod.RIGHT_KEY.isDown(), PlaneClientMod.BRAKE_KEY.isDown(), PlaneClientMod.START_KEY.isDown());
        }

        if (PlaneClientMod.PLANE_KEY.consumeClick()) {
            if ((event.getModifiers() & GLFW.GLFW_MOD_CONTROL) == GLFW.GLFW_MOD_CONTROL) {
                PlaneMod.CLIENT_CONFIG.showPlaneInfo.set(!PlaneMod.CLIENT_CONFIG.showPlaneInfo.get());
                PlaneMod.CLIENT_CONFIG.showPlaneInfo.save();
            } else {
                plane.openGUI(player, false);
            }
        }
    }

}
