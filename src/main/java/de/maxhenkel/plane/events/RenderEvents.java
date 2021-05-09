package de.maxhenkel.plane.events;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.maxhenkel.plane.Main;
import de.maxhenkel.plane.entity.EntityPlaneSoundBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import de.maxhenkel.corelib.math.MathUtils;

import java.util.List;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class RenderEvents {

    private static final ResourceLocation PLANE_INFO_TEXTURE = new ResourceLocation(Main.MODID, "textures/gui/plane_info.png");

    private Minecraft mc;
    private EntityPlaneSoundBase lastVehicle;

    public RenderEvents() {
        mc = Minecraft.getInstance();
    }

    @SubscribeEvent
    public void onRender(EntityViewRenderEvent.CameraSetup evt) {
        if (getPlane() != null && !mc.options.getCameraType().isFirstPerson()) {
            evt.getInfo().move(-evt.getInfo().getMaxZoom(Main.CLIENT_CONFIG.planeZoom.get() - 4D), 0D, 0D);
        }
    }

    @SubscribeEvent
    public void onRender(InputEvent.MouseScrollEvent evt) {
        if (getPlane() != null && !mc.options.getCameraType().isFirstPerson()) {
            Main.CLIENT_CONFIG.planeZoom.set(Math.max(1D, Math.min(20D, Main.CLIENT_CONFIG.planeZoom.get() - evt.getScrollDelta())));
            Main.CLIENT_CONFIG.planeZoom.save();
            evt.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent evt) {
        if (!evt.getType().equals(ElementType.EXPERIENCE)) {
            return;
        }

        PlayerEntity player = mc.player;

        Entity e = player.getVehicle();

        if (!(e instanceof EntityPlaneSoundBase)) {
            return;
        }

        EntityPlaneSoundBase plane = (EntityPlaneSoundBase) e;

        if (Main.CLIENT_CONFIG.showPlaneInfo.get()) {
            renderPlaneInfo(evt.getMatrixStack(), plane);
        }
    }

    public void renderPlaneInfo(MatrixStack matrixStack, EntityPlaneSoundBase plane) {
        matrixStack.pushPose();

        mc.getTextureManager().bind(PLANE_INFO_TEXTURE);

        int texWidth = 110;
        int texHeight = 90;

        int height = mc.getWindow().getGuiScaledHeight();
        int width = mc.getWindow().getGuiScaledWidth();

        float scale = Main.CLIENT_CONFIG.planeInfoScale.get().floatValue();
        matrixStack.scale(scale, scale, 1F);
        matrixStack.translate(-width, -height, 0D);
        matrixStack.translate(((double) width) * (1D / scale), ((double) height * (1D / scale)), 0D);

        int padding = 3;
        int yStart = height - texHeight - padding;
        int xStart = width - texWidth - padding;

        mc.gui.blit(matrixStack, xStart, yStart, 0, 0, texWidth, texHeight);

        FontRenderer font = mc.gui.getFont();

        Function<Integer, Integer> heightFunc = integer -> yStart + 8 + (font.lineHeight + 2) * integer;

        font.draw(matrixStack, new TranslationTextComponent("tooltip.plane.speed", Main.CLIENT_CONFIG.planeInfoSpeedType.get().getTextComponent(plane.getDeltaMovement().length())).getVisualOrderText(), xStart + 7, heightFunc.apply(0), 0);
        font.draw(matrixStack, new TranslationTextComponent("tooltip.plane.vertical_speed", Main.CLIENT_CONFIG.planeInfoSpeedType.get().getTextComponent(plane.getDeltaMovement().y())).getVisualOrderText(), xStart + 7, heightFunc.apply(1), 0);
        font.draw(matrixStack, new TranslationTextComponent("tooltip.plane.throttle", String.valueOf(Math.round(plane.getEngineSpeed() * 100F))).getVisualOrderText(), xStart + 7, heightFunc.apply(2), 0);
        font.draw(matrixStack, new TranslationTextComponent("tooltip.plane.height", String.valueOf(Math.round(plane.getY()))).getVisualOrderText(), xStart + 7, heightFunc.apply(3), 0);
        font.draw(matrixStack, new TranslationTextComponent("tooltip.plane.relative_height", String.valueOf(Math.round(cachedRelativeHeight))).getVisualOrderText(), xStart + 7, heightFunc.apply(4), 0);
        font.draw(matrixStack, new TranslationTextComponent("tooltip.plane.fuel", String.valueOf(plane.getFuel())).getVisualOrderText(), xStart + 7, heightFunc.apply(5), 0);
        font.draw(matrixStack, new TranslationTextComponent("tooltip.plane.damage", String.valueOf(MathUtils.round(plane.getPlaneDamage(), 2))).getVisualOrderText(), xStart + 7, heightFunc.apply(6), 0);

        matrixStack.popPose();
    }

    private double cachedRelativeHeight = 0D;

    private double getRelativeHeight(EntityPlaneSoundBase plane) {
        int highestBlock = (int) plane.getY();
        BlockPos.Mutable p = new BlockPos.Mutable(plane.getX(), plane.getY(), plane.getZ());
        for (int y = highestBlock; y >= 0; y--) {
            p.setY(y);
            if (plane.level.getBlockState(p).canOcclude()) {
                highestBlock = y;
                break;
            }
        }

        return plane.getY() - (double) (highestBlock + 1);
    }

    @SubscribeEvent
    public void renderPlayerPre(RenderPlayerEvent.Pre event) {
        PlayerEntity player = event.getPlayer();
        if (player.getVehicle() instanceof EntityPlaneSoundBase) {
            EntityPlaneSoundBase plane = (EntityPlaneSoundBase) event.getPlayer().getVehicle();
            event.getMatrixStack().pushPose();

            event.getMatrixStack().mulPose(Vector3f.YP.rotationDegrees(-(plane.yRotO + (plane.yRot - plane.yRotO) * event.getPartialRenderTick())));
            event.getMatrixStack().mulPose(Vector3f.XP.rotationDegrees(plane.xRotO + (plane.xRot - plane.xRotO) * event.getPartialRenderTick()));

            List<Entity> passengers = plane.getPassengers();
            int i = passengers.indexOf(player);
            if (i >= 0) {
                Vector3d offset = plane.getPlayerOffsets()[i];
                offset = offset.xRot((float) -Math.toRadians(plane.xRot));
                event.getMatrixStack().translate(0F, offset.y, 0F);
            }

            event.getMatrixStack().scale(plane.getPlayerScaleFactor(), plane.getPlayerScaleFactor(), plane.getPlayerScaleFactor());
            event.getMatrixStack().translate(0F, (player.getBbHeight() - (player.getBbHeight() * plane.getPlayerScaleFactor())) / 1.5F + (float) plane.getPlayerOffsets()[0].y, 0F);

            event.getMatrixStack().mulPose(Vector3f.YP.rotationDegrees(plane.yRotO + (plane.yRot - plane.yRotO) * event.getPartialRenderTick()));
        }
    }

    @SubscribeEvent
    public void renderPlayerPost(RenderPlayerEvent.Post event) {
        if (event.getPlayer().getVehicle() instanceof EntityPlaneSoundBase) {
            event.getMatrixStack().popPose();
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent evt) {
        if (!evt.player.equals(mc.player)) {
            return;
        }

        EntityPlaneSoundBase vehicle = getPlane();

        if (vehicle != null && evt.player.equals(vehicle.getDriver())) {
            cachedRelativeHeight = getRelativeHeight(vehicle);
        }

        if (vehicle != null && lastVehicle == null) {
            mc.options.setCameraType(PointOfView.THIRD_PERSON_BACK);
        } else if (vehicle == null && lastVehicle != null) {
            mc.options.setCameraType(PointOfView.FIRST_PERSON);
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
