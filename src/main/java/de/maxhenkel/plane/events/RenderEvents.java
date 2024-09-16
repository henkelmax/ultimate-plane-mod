package de.maxhenkel.plane.events;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import de.maxhenkel.corelib.math.MathUtils;
import de.maxhenkel.plane.Main;
import de.maxhenkel.plane.entity.EntityPlaneSoundBase;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.List;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class RenderEvents {

    private static final ResourceLocation PLANE_INFO_TEXTURE = ResourceLocation.fromNamespaceAndPath(Main.MODID, "textures/gui/plane_info.png");

    private Minecraft mc;
    private EntityPlaneSoundBase lastVehicle;

    public RenderEvents() {
        mc = Minecraft.getInstance();
    }

    @SubscribeEvent
    public void onCameraDistance(CalculateDetachedCameraDistanceEvent evt) {
        if (getPlane() != null && !mc.options.getCameraType().isFirstPerson()) {
            evt.setDistance(Main.CLIENT_CONFIG.planeZoom.get().floatValue());
        }
    }

    @SubscribeEvent
    public void onScroll(InputEvent.MouseScrollingEvent evt) {
        if (getPlane() != null && !mc.options.getCameraType().isFirstPerson()) {
            Main.CLIENT_CONFIG.planeZoom.set(Math.max(1D, Math.min(20D, Main.CLIENT_CONFIG.planeZoom.get() - evt.getScrollDeltaY())));
            Main.CLIENT_CONFIG.planeZoom.save();
            evt.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onRender(RenderGuiLayerEvent.Post evt) {
        if (!VanillaGuiLayers.HOTBAR.equals(evt.getName())) {
            return;
        }

        Player player = mc.player;

        Entity e = player.getVehicle();

        if (!(e instanceof EntityPlaneSoundBase)) {
            return;
        }

        EntityPlaneSoundBase plane = (EntityPlaneSoundBase) e;

        if (Main.CLIENT_CONFIG.showPlaneInfo.get()) {
            renderPlaneInfo(evt.getGuiGraphics(), plane);
        }
    }

    public void renderPlaneInfo(GuiGraphics guiGraphics, EntityPlaneSoundBase plane) {
        guiGraphics.pose().pushPose();

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

        int texWidth = 110;
        int texHeight = 90;

        int height = mc.getWindow().getGuiScaledHeight();
        int width = mc.getWindow().getGuiScaledWidth();

        float scale = Main.CLIENT_CONFIG.planeInfoScale.get().floatValue();
        guiGraphics.pose().scale(scale, scale, 1F);
        guiGraphics.pose().translate(-width, -height, 0D);
        guiGraphics.pose().translate(((double) width) * (1D / scale), ((double) height * (1D / scale)), 0D);

        int padding = 3;
        int yStart = height - texHeight - padding;
        int xStart = width - texWidth - padding;

        guiGraphics.blit(PLANE_INFO_TEXTURE, xStart, yStart, 0, 0, texWidth, texHeight);

        Font font = mc.gui.getFont();

        Function<Integer, Integer> heightFunc = integer -> yStart + 8 + (font.lineHeight + 2) * integer;

        guiGraphics.drawString(font, Component.translatable("tooltip.plane.speed", Main.CLIENT_CONFIG.planeInfoSpeedType.get().getTextComponent(plane.getDeltaMovement().length())).getVisualOrderText(), xStart + 7, heightFunc.apply(0), 0, false);
        guiGraphics.drawString(font, Component.translatable("tooltip.plane.vertical_speed", Main.CLIENT_CONFIG.planeInfoSpeedType.get().getTextComponent(plane.getDeltaMovement().y())).getVisualOrderText(), xStart + 7, heightFunc.apply(1), 0, false);
        guiGraphics.drawString(font, Component.translatable("tooltip.plane.throttle", String.valueOf(Math.round(plane.getEngineSpeed() * 100F))).getVisualOrderText(), xStart + 7, heightFunc.apply(2), 0, false);
        guiGraphics.drawString(font, Component.translatable("tooltip.plane.height", String.valueOf(Math.round(plane.getY()))).getVisualOrderText(), xStart + 7, heightFunc.apply(3), 0, false);
        guiGraphics.drawString(font, Component.translatable("tooltip.plane.relative_height", String.valueOf(Math.round(cachedRelativeHeight))).getVisualOrderText(), xStart + 7, heightFunc.apply(4), 0, false);
        guiGraphics.drawString(font, Component.translatable("tooltip.plane.fuel", String.valueOf(plane.getFuel())).getVisualOrderText(), xStart + 7, heightFunc.apply(5), 0, false);
        guiGraphics.drawString(font, Component.translatable("tooltip.plane.damage", String.valueOf(MathUtils.round(plane.getPlaneDamage(), 2))).getVisualOrderText(), xStart + 7, heightFunc.apply(6), 0, false);

        guiGraphics.pose().popPose();
    }

    private double cachedRelativeHeight = 0D;

    private double getRelativeHeight(EntityPlaneSoundBase plane) {
        int highestBlock = (int) plane.getY();
        BlockPos.MutableBlockPos p = new BlockPos.MutableBlockPos(plane.getX(), plane.getY(), plane.getZ());
        for (int y = highestBlock; y >= plane.level().getMinBuildHeight(); y--) {
            p.setY(y);
            if (plane.level().getBlockState(p).canOcclude()) {
                highestBlock = y;
                break;
            }
        }

        return plane.getY() - (double) (highestBlock + 1);
    }

    @SubscribeEvent
    public void renderPlayerPre(RenderPlayerEvent.Pre event) {
        Player player = event.getEntity();
        if (player.getVehicle() instanceof EntityPlaneSoundBase) {
            EntityPlaneSoundBase plane = (EntityPlaneSoundBase) event.getEntity().getVehicle();
            event.getPoseStack().pushPose();

            event.getPoseStack().mulPose(Axis.YP.rotationDegrees(-(plane.yRotO + (plane.getYRot() - plane.yRotO) * event.getPartialTick())));
            event.getPoseStack().mulPose(Axis.XP.rotationDegrees(plane.xRotO + (plane.getXRot() - plane.xRotO) * event.getPartialTick()));

            List<Entity> passengers = plane.getPassengers();
            int i = passengers.indexOf(player);
            if (i >= 0) {
                Vec3 offset = plane.getPlayerOffsets()[i];
                offset = offset.xRot((float) -Math.toRadians(plane.getXRot()));
                event.getPoseStack().translate(0F, offset.y, 0F);
            }

            event.getPoseStack().scale(plane.getPlayerScaleFactor(), plane.getPlayerScaleFactor(), plane.getPlayerScaleFactor());
            event.getPoseStack().translate(0F, (player.getBbHeight() - (player.getBbHeight() * plane.getPlayerScaleFactor())) / 1.5F + (float) plane.getPlayerOffsets()[0].y, 0F);

            event.getPoseStack().mulPose(Axis.YP.rotationDegrees(plane.yRotO + (plane.getYRot() - plane.yRotO) * event.getPartialTick()));
        }
    }

    @SubscribeEvent
    public void renderPlayerPost(RenderPlayerEvent.Post event) {
        if (event.getEntity().getVehicle() instanceof EntityPlaneSoundBase) {
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
