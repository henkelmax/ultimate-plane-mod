package de.maxhenkel.plane.events;

import de.maxhenkel.plane.MathTools;
import de.maxhenkel.plane.entity.EntityPlane;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
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
        percent = MathHelper.clamp(percent, 0F, 1F);
        int x = mc.getMainWindow().getScaledWidth() / 2 - 91;

        mc.getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);

        int k = mc.getMainWindow().getScaledHeight() - 32 + 3;
        mc.ingameGUI.blit(x, k, 0, 64, 182, 5);

        int j = (int) (percent * 182F);

        if (j > 0) {
            mc.ingameGUI.blit(x, k, 0, 69, j, 5);
        }
    }

    public void renderSpeed(double speed) {
        String s = String.valueOf(MathTools.round(Math.abs(speed), 2));
        int i1 = (mc.getMainWindow().getScaledWidth() - mc.ingameGUI.getFontRenderer().getStringWidth(s)) / 2;
        int j1 = mc.getMainWindow().getScaledHeight() - 31 - 4;
        mc.ingameGUI.getFontRenderer().drawString(s, i1 + 1, j1, 0);
        mc.ingameGUI.getFontRenderer().drawString(s, i1 - 1, j1, 0);
        mc.ingameGUI.getFontRenderer().drawString(s, i1, j1 + 1, 0);
        mc.ingameGUI.getFontRenderer().drawString(s, i1, j1 - 1, 0);
        mc.ingameGUI.getFontRenderer().drawString(s, i1, j1, 8453920);

    }

    @SubscribeEvent
    public void renderPlayerPre(RenderPlayerEvent.Pre event) {
        PlayerEntity player = event.getPlayer();
        if (player.getRidingEntity() instanceof EntityPlane) {
            EntityPlane plane = (EntityPlane) event.getPlayer().getRidingEntity();
            event.getMatrixStack().push();
            event.getMatrixStack().rotate(Vector3f.YP.rotationDegrees(-(plane.rotationYaw + (plane.rotationYaw - plane.prevRotationYaw) * event.getPartialRenderTick())));
            event.getMatrixStack().rotate(Vector3f.XP.rotationDegrees(plane.rotationPitch + (plane.rotationPitch - plane.prevRotationPitch) * event.getPartialRenderTick()));
            event.getMatrixStack().scale(EntityPlane.SCALE_FACTOR, EntityPlane.SCALE_FACTOR, EntityPlane.SCALE_FACTOR);
            event.getMatrixStack().translate(0F, (player.getHeight() - (player.getHeight() * EntityPlane.SCALE_FACTOR)) / 1.5F + (float) plane.getPlayerOffsets()[0].y, 0F);
            event.getMatrixStack().rotate(Vector3f.YP.rotationDegrees(plane.rotationYaw + (plane.rotationYaw - plane.prevRotationYaw) * event.getPartialRenderTick()));
        }
    }

    @SubscribeEvent
    public void renderPlayerPost(RenderPlayerEvent.Post event) {
        if (event.getPlayer().getRidingEntity() instanceof EntityPlane) {
            event.getMatrixStack().pop();
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
