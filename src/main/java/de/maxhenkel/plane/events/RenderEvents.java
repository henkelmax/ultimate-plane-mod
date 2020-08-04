package de.maxhenkel.plane.events;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.maxhenkel.corelib.math.MathUtils;
import de.maxhenkel.plane.Main;
import de.maxhenkel.plane.entity.EntityPlaneSoundBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

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
    public void onRender(RenderGameOverlayEvent evt) {
        if (!evt.getType().equals(ElementType.EXPERIENCE)) {
            return;
        }

        PlayerEntity player = mc.player;

        Entity e = player.getRidingEntity();

        if (!(e instanceof EntityPlaneSoundBase)) {
            return;
        }

        EntityPlaneSoundBase plane = (EntityPlaneSoundBase) e;

        if (Main.CLIENT_CONFIG.showPlaneInfo.get()) {
            renderPlaneInfo(evt.getMatrixStack(), plane);
        }
    }

    public void renderPlaneInfo(MatrixStack matrixStack, EntityPlaneSoundBase plane) {
        matrixStack.push();

        mc.getTextureManager().bindTexture(PLANE_INFO_TEXTURE);

        int texWidth = 110;
        int texHeight = 90;

        int height = mc.getMainWindow().getScaledHeight();
        int width = mc.getMainWindow().getScaledWidth();

        float scale = Main.CLIENT_CONFIG.planeInfoScale.get().floatValue();
        matrixStack.scale(scale, scale, 1F);
        matrixStack.translate(-width, -height, 0D);
        matrixStack.translate(((double) width) * (1D / scale), ((double) height * (1D / scale)), 0D);

        int padding = 3;
        int yStart = height - texHeight - padding;
        int xStart = width - texWidth - padding;

        mc.ingameGUI.func_238474_b_(matrixStack, xStart, yStart, 0, 0, texWidth, texHeight);

        FontRenderer font = mc.ingameGUI.getFontRenderer();

        Function<Integer, Integer> heightFunc = integer -> yStart + 8 + (font.FONT_HEIGHT + 2) * integer;

        font.func_238422_b_(matrixStack, new TranslationTextComponent("tooltip.plane.speed", Main.CLIENT_CONFIG.planeInfoSpeedType.get().getTextComponent(plane.getMotion().length())), xStart + 7, heightFunc.apply(0), 0);
        font.func_238422_b_(matrixStack, new TranslationTextComponent("tooltip.plane.vertical_speed", Main.CLIENT_CONFIG.planeInfoSpeedType.get().getTextComponent(plane.getMotion().getY())), xStart + 7, heightFunc.apply(1), 0);
        font.func_238422_b_(matrixStack, new TranslationTextComponent("tooltip.plane.throttle", Math.round(plane.getEngineSpeed() * 100F)), xStart + 7, heightFunc.apply(2), 0);
        font.func_238422_b_(matrixStack, new TranslationTextComponent("tooltip.plane.height", Math.round(plane.getPosY())), xStart + 7, heightFunc.apply(3), 0);
        font.func_238422_b_(matrixStack, new TranslationTextComponent("tooltip.plane.relative_height", Math.round(cachedRelativeHeight)), xStart + 7, heightFunc.apply(4), 0);
        font.func_238422_b_(matrixStack, new TranslationTextComponent("tooltip.plane.fuel", plane.getFuel()), xStart + 7, heightFunc.apply(5), 0);
        font.func_238422_b_(matrixStack, new TranslationTextComponent("tooltip.plane.damage", MathUtils.round(plane.getPlaneDamage(), 2)), xStart + 7, heightFunc.apply(6), 0);

        matrixStack.pop();
    }

    private double cachedRelativeHeight = 0D;

    private double getRelativeHeight(EntityPlaneSoundBase plane) {
        int highestBlock = (int) plane.getPosY();
        BlockPos.Mutable p = new BlockPos.Mutable(plane.getPosX(), plane.getPosY(), plane.getPosZ());
        for (int y = highestBlock; y >= 0; y--) {
            p.setY(y);
            if (plane.world.getBlockState(p).isSolid()) {
                highestBlock = y;
                break;
            }
        }

        return plane.getPosY() - (double) (highestBlock + 1);
    }

    @SubscribeEvent
    public void renderPlayerPre(RenderPlayerEvent.Pre event) {
        PlayerEntity player = event.getPlayer();
        if (player.getRidingEntity() instanceof EntityPlaneSoundBase) {
            EntityPlaneSoundBase plane = (EntityPlaneSoundBase) event.getPlayer().getRidingEntity();
            event.getMatrixStack().push();

            event.getMatrixStack().rotate(Vector3f.YP.rotationDegrees(-(plane.rotationYaw + (plane.rotationYaw - plane.prevRotationYaw) * event.getPartialRenderTick())));
            event.getMatrixStack().rotate(Vector3f.XP.rotationDegrees(plane.rotationPitch + (plane.rotationPitch - plane.prevRotationPitch) * event.getPartialRenderTick()));

            List<Entity> passengers = plane.getPassengers();
            int i = passengers.indexOf(player);
            if (i >= 0) {
                Vector3d offset = plane.getPlayerOffsets()[i];
                offset = offset.rotatePitch((float) -Math.toRadians(plane.rotationPitch));
                event.getMatrixStack().translate(0F, offset.y, 0F);
            }

            event.getMatrixStack().scale(plane.getPlayerScaleFactor(), plane.getPlayerScaleFactor(), plane.getPlayerScaleFactor());
            event.getMatrixStack().translate(0F, (player.getHeight() - (player.getHeight() * plane.getPlayerScaleFactor())) / 1.5F + (float) plane.getPlayerOffsets()[0].y, 0F);

            event.getMatrixStack().rotate(Vector3f.YP.rotationDegrees(plane.rotationYaw + (plane.rotationYaw - plane.prevRotationYaw) * event.getPartialRenderTick()));
        }
    }

    @SubscribeEvent
    public void renderPlayerPost(RenderPlayerEvent.Post event) {
        if (event.getPlayer().getRidingEntity() instanceof EntityPlaneSoundBase) {
            event.getMatrixStack().pop();
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
            mc.gameSettings.thirdPersonView = 1;
        } else if (vehicle == null && lastVehicle != null) {
            mc.gameSettings.thirdPersonView = 0;
        }
        lastVehicle = vehicle;
    }

    private EntityPlaneSoundBase getPlane() {
        if (mc.player.getRidingEntity() instanceof EntityPlaneSoundBase) {
            return (EntityPlaneSoundBase) mc.player.getRidingEntity();
        }
        return null;
    }

}
