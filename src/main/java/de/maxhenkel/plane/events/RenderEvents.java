package de.maxhenkel.plane.events;

import com.mojang.blaze3d.platform.GlStateManager;
import de.maxhenkel.plane.MathTools;
import de.maxhenkel.plane.entity.EntityPlane;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class RenderEvents {

    private Minecraft mc;
    private EntityPlane lastVehicle;

    public RenderEvents() {
        mc = Minecraft.getInstance();
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent evt) {
        if (!evt.getType().equals(ElementType.EXPERIENCE)) {
            return;
        }

        PlayerEntity player = mc.player;

        Entity e = player.getRidingEntity();

        if (!(e instanceof EntityPlane)) {
            return;
        }

        EntityPlane plane = (EntityPlane) e;

        if (player.equals(plane.getDriver())) {
            evt.setCanceled(true);
            renderFuelBar(plane.getEngineSpeed());
            renderSpeed((plane.getMotion().mul(1D, 0D, 1D).length() * 20D * 60D * 60D) / 1000D);
        }

    }

    public void renderFuelBar(double percent) {
        int x = mc.mainWindow.getScaledWidth() / 2 - 91;

        mc.getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);

        int k = mc.mainWindow.getScaledHeight() - 32 + 3;
        mc.ingameGUI.blit(x, k, 0, 64, 182, 5);

        int j = (int) (percent * 182.0F);

        if (j > 0) {
            mc.ingameGUI.blit(x, k, 0, 69, j, 5);
        }
    }

    public void renderSpeed(double speed) {
        String s = String.valueOf(MathTools.round(Math.abs(speed), 2));
        int i1 = (mc.mainWindow.getScaledWidth() - mc.ingameGUI.getFontRenderer().getStringWidth(s)) / 2;
        int j1 = mc.mainWindow.getScaledHeight() - 31 - 4;
        mc.ingameGUI.getFontRenderer().drawString(s, i1 + 1, j1, 0);
        mc.ingameGUI.getFontRenderer().drawString(s, i1 - 1, j1, 0);
        mc.ingameGUI.getFontRenderer().drawString(s, i1, j1 + 1, 0);
        mc.ingameGUI.getFontRenderer().drawString(s, i1, j1 - 1, 0);
        mc.ingameGUI.getFontRenderer().drawString(s, i1, j1, 8453920);

    }

    @SubscribeEvent
    public void renderPlayerPre(RenderPlayerEvent.Pre event) {
        PlayerEntity player = event.getEntityPlayer();
        if (player.getRidingEntity() instanceof EntityPlane) {
            EntityPlane plane = (EntityPlane) event.getEntityPlayer().getRidingEntity();
            GlStateManager.pushMatrix();
            GlStateManager.translated(event.getX(), event.getY(), event.getZ());
            GlStateManager.rotatef(-(plane.rotationYaw + (plane.rotationYaw - plane.prevRotationYaw) * event.getPartialRenderTick()), 0F, 1F, 0F);
            GlStateManager.rotatef(plane.rotationPitch + (plane.rotationPitch - plane.prevRotationPitch) * event.getPartialRenderTick(), 1F, 0F, 0F);
            GlStateManager.scalef(EntityPlane.SCALE_FACTOR, EntityPlane.SCALE_FACTOR, EntityPlane.SCALE_FACTOR);
            GlStateManager.translatef(0F, (event.getEntityPlayer().getHeight() - (event.getEntityPlayer().getHeight() * EntityPlane.SCALE_FACTOR)) / 1.5F + (float) plane.getPlayerOffsets()[0].y, 0F);
            GlStateManager.rotatef(plane.rotationYaw + (plane.rotationYaw - plane.prevRotationYaw) * event.getPartialRenderTick(), 0F, 1F, 0F);
            GlStateManager.translated(-event.getX(), -event.getY(), -event.getZ());
        }
    }

    @SubscribeEvent
    public void renderPlayerPost(RenderPlayerEvent.Post event) {
        if (event.getEntityPlayer().getRidingEntity() instanceof EntityPlane) {
            GlStateManager.popMatrix();
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent evt) {
        if (!evt.player.equals(mc.player)) {
            return;
        }

        EntityPlane vehicle = getPlane();

        if (vehicle != null && lastVehicle == null) {
            mc.gameSettings.thirdPersonView = 1;
        } else if (vehicle == null && lastVehicle != null) {
            mc.gameSettings.thirdPersonView = 0;
        }
        lastVehicle = vehicle;
    }

    private EntityPlane getPlane() {
        if (mc.player.getRidingEntity() instanceof EntityPlane) {
            return (EntityPlane) mc.player.getRidingEntity();
        }
        return null;
    }
}
