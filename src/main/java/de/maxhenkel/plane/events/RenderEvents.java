package de.maxhenkel.plane.events;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.maxhenkel.corelib.FontColorUtils;
import de.maxhenkel.corelib.math.MathUtils;
import de.maxhenkel.plane.PlaneMod;
import de.maxhenkel.plane.entity.EntityPlaneBase;
import de.maxhenkel.plane.entity.EntityPlaneSoundBase;
import net.minecraft.ChatFormatting;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.List;
import java.util.function.Function;

public class RenderEvents {

    private static final Identifier PLANE_INFO_TEXTURE = Identifier.fromNamespaceAndPath(PlaneMod.MODID, "textures/gui/plane_info.png");

    private Minecraft mc;
    private EntityPlaneSoundBase lastVehicle;

    public RenderEvents() {
        mc = Minecraft.getInstance();
    }

    @SubscribeEvent
    public void onCameraDistance(CalculateDetachedCameraDistanceEvent evt) {
        if (getPlane() != null && !mc.options.getCameraType().isFirstPerson()) {
            evt.setDistance(PlaneMod.CLIENT_CONFIG.planeZoom.get().floatValue());
        }
    }

    @SubscribeEvent
    public void onScroll(InputEvent.MouseScrollingEvent evt) {
        if (getPlane() != null && !mc.options.getCameraType().isFirstPerson()) {
            PlaneMod.CLIENT_CONFIG.planeZoom.set(Math.max(1D, Math.min(20D, PlaneMod.CLIENT_CONFIG.planeZoom.get() - evt.getScrollDeltaY())));
            PlaneMod.CLIENT_CONFIG.planeZoom.save();
            evt.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onRender(RenderGuiLayerEvent.Post evt) {
        if (!VanillaGuiLayers.HOTBAR.equals(evt.getName())) {
            return;
        }
        if (mc.options.hideGui) {
            return;
        }

        Player player = mc.player;

        Entity e = player.getVehicle();

        if (!(e instanceof EntityPlaneBase plane)) {
            return;
        }

        if (PlaneMod.CLIENT_CONFIG.showPlaneInfo.get()) {
            renderPlaneInfo(evt.getGuiGraphics(), plane);
        }
    }

    public void renderPlaneInfo(GuiGraphics guiGraphics, EntityPlaneSoundBase plane) {
        guiGraphics.pose().pushMatrix();

        int texWidth = 110;
        int texHeight = 90;

        int height = mc.getWindow().getGuiScaledHeight();
        int width = mc.getWindow().getGuiScaledWidth();

        float scale = PlaneMod.CLIENT_CONFIG.planeInfoScale.get().floatValue();
        guiGraphics.pose().scale(scale, scale);
        guiGraphics.pose().translate(-width, -height);
        guiGraphics.pose().translate(width * (1F / scale), (height * (1F / scale)));

        int padding = 3;
        int yStart = height - texHeight - padding;
        int xStart = width - texWidth - padding;

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, PLANE_INFO_TEXTURE, xStart, yStart, 0, 0, texWidth, texHeight, 256, 256);

        Font font = mc.gui.getFont();

        Function<Integer, Integer> heightFunc = integer -> yStart + 8 + (font.lineHeight + 2) * integer;

        int black = FontColorUtils.getFontColor(ChatFormatting.BLACK);
        guiGraphics.drawString(font, Component.translatable("tooltip.plane.speed", PlaneMod.CLIENT_CONFIG.planeInfoSpeedType.get().getTextComponent(plane.getDeltaMovement().length())).getVisualOrderText(), xStart + 7, heightFunc.apply(0), black, false);
        guiGraphics.drawString(font, Component.translatable("tooltip.plane.vertical_speed", PlaneMod.CLIENT_CONFIG.planeInfoSpeedType.get().getTextComponent(plane.getDeltaMovement().y())).getVisualOrderText(), xStart + 7, heightFunc.apply(1), black, false);
        guiGraphics.drawString(font, Component.translatable("tooltip.plane.throttle", String.valueOf(Math.round(plane.getEngineSpeed() * 100F))).getVisualOrderText(), xStart + 7, heightFunc.apply(2), black, false);
        guiGraphics.drawString(font, Component.translatable("tooltip.plane.height", String.valueOf(Math.round(plane.getY()))).getVisualOrderText(), xStart + 7, heightFunc.apply(3), black, false);
        guiGraphics.drawString(font, Component.translatable("tooltip.plane.relative_height", String.valueOf(Math.round(cachedRelativeHeight))).getVisualOrderText(), xStart + 7, heightFunc.apply(4), black, false);
        guiGraphics.drawString(font, Component.translatable("tooltip.plane.fuel", String.valueOf(plane.getFuel())).getVisualOrderText(), xStart + 7, heightFunc.apply(5), black, false);
        guiGraphics.drawString(font, Component.translatable("tooltip.plane.damage", String.valueOf(MathUtils.round(plane.getPlaneDamage(), 2))).getVisualOrderText(), xStart + 7, heightFunc.apply(6), black, false);

        guiGraphics.pose().popMatrix();
    }

    private double cachedRelativeHeight = 0D;

    private double getRelativeHeight(EntityPlaneSoundBase plane) {
        int highestBlock = (int) plane.getY();
        BlockPos.MutableBlockPos p = new BlockPos.MutableBlockPos(plane.getX(), plane.getY(), plane.getZ());
        for (int y = highestBlock; y >= plane.level().getMinY(); y--) {
            p.setY(y);
            if (plane.level().getBlockState(p).canOcclude()) {
                highestBlock = y;
                break;
            }
        }

        return plane.getY() - (double) (highestBlock + 1);
    }

    @SubscribeEvent
    public void renderPlayerPre(RenderPlayerEvent.Pre<AbstractClientPlayer> event) {
        if (mc.level == null) {
            return;
        }
        Entity entity = mc.level.getEntity(event.getRenderState().id);
        if (!(entity instanceof Player player)) {
            return;
        }
        if (player.getVehicle() instanceof EntityPlaneBase plane) {
            PoseStack pose = event.getPoseStack();
            pose.pushPose();

            pose.mulPose(Axis.YP.rotationDegrees(-(plane.yRotO + (plane.getYRot() - plane.yRotO) * event.getPartialTick())));
            Vec3 bodyRotationCenter = plane.getBodyRotationCenter();
            pose.mulPose(Axis.XP.rotationDegrees(plane.xRotO + (plane.getXRot() - plane.xRotO) * event.getPartialTick()));

            List<Entity> passengers = plane.getPassengers();
            int i = passengers.indexOf(player);
            if (i >= 0) {
                Vec3 offset = plane.getPlayerOffsets()[i];
                offset = offset.add(bodyRotationCenter);
                offset = offset.xRot((float) -Math.toRadians(plane.getXRot()));
                pose.translate(0F, offset.y, 0F);
            }

            pose.scale(plane.getPlayerScaleFactor(), plane.getPlayerScaleFactor(), plane.getPlayerScaleFactor());
            pose.translate(0F, (player.getBbHeight() - (player.getBbHeight() * plane.getPlayerScaleFactor())) / 1.5F + (float) plane.getPlayerOffsets()[0].y, 0F);

            pose.mulPose(Axis.YP.rotationDegrees(plane.yRotO + (plane.getYRot() - plane.yRotO) * event.getPartialTick()));
        }
    }

    @SubscribeEvent
    public void renderPlayerPost(RenderPlayerEvent.Post<AbstractClientPlayer> event) {
        if (mc.level == null) {
            return;
        }
        Entity entity = mc.level.getEntity(event.getRenderState().id);
        if (!(entity instanceof Player player)) {
            return;
        }
        if (player.getVehicle() instanceof EntityPlaneBase) {
            event.getPoseStack().popPose();
        }
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Pre evt) {
        if (!evt.getEntity().equals(mc.player)) {
            return;
        }

        EntityPlaneSoundBase vehicle = getPlane();

        if (vehicle != null && evt.getEntity().equals(vehicle.getDriver())) {
            cachedRelativeHeight = getRelativeHeight(vehicle);
        }

        if (vehicle != null && lastVehicle == null) {
            mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);
        } else if (vehicle == null && lastVehicle != null) {
            mc.options.setCameraType(CameraType.FIRST_PERSON);
        }
        lastVehicle = vehicle;
    }

    private EntityPlaneSoundBase getPlane() {
        if (mc.player.getVehicle() instanceof EntityPlaneSoundBase) {
            return (EntityPlaneSoundBase) mc.player.getVehicle();
        }
        return null;
    }

}
