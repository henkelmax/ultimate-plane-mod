package de.maxhenkel.plane.events;

import com.mojang.blaze3d.systems.RenderSystem;
import de.maxhenkel.plane.Config;
import de.maxhenkel.plane.Main;
import de.maxhenkel.plane.MathTools;
import de.maxhenkel.plane.entity.EntityPlane;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
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

        if (Config.SHOW_PLANE_INFO.get()) {
            renderPlaneInfo(plane);
        }
    }

    public void renderPlaneInfo(EntityPlane plane) {
        RenderSystem.pushMatrix();

        mc.getTextureManager().bindTexture(PLANE_INFO_TEXTURE);

        int texWidth = 110;
        int texHeight = 90;

        int height = mc.getMainWindow().getScaledHeight();
        int width = mc.getMainWindow().getScaledWidth();

        double scale = Config.PLANE_INFO_SCALE.get();
        RenderSystem.scaled(scale, scale, 1D);
        RenderSystem.translated(-width, -height, 0F);
        RenderSystem.translated(((double) width) * (1D / scale), ((double) height * (1D / scale)), 0F);

        int padding = 3;
        int yStart = height - texHeight - padding;
        int xStart = width - texWidth - padding;

        mc.ingameGUI.blit(xStart, yStart, 0, 0, texWidth, texHeight);

        FontRenderer font = mc.ingameGUI.getFontRenderer();

        Function<Integer, Integer> heightFunc = integer -> yStart + 8 + (font.FONT_HEIGHT + 2) * integer;

        font.drawString(new TranslationTextComponent("tooltip.plane.speed", Config.PLANE_INFO_SPEED_TYPE.get().getTextComponent(plane.getMotion().length())).getFormattedText(), xStart + 7, heightFunc.apply(0), 0);
        font.drawString(new TranslationTextComponent("tooltip.plane.vertical_speed", Config.PLANE_INFO_SPEED_TYPE.get().getTextComponent(plane.getMotion().getY())).getFormattedText(), xStart + 7, heightFunc.apply(1), 0);
        font.drawString(new TranslationTextComponent("tooltip.plane.throttle", Math.round(plane.getEngineSpeed() * 100F)).getFormattedText(), xStart + 7, heightFunc.apply(2), 0);
        font.drawString(new TranslationTextComponent("tooltip.plane.height", Math.round(plane.getPosY())).getFormattedText(), xStart + 7, heightFunc.apply(3), 0);
        font.drawString(new TranslationTextComponent("tooltip.plane.relative_height", Math.round(cachedRelativeHeight)).getFormattedText(), xStart + 7, heightFunc.apply(4), 0);
        font.drawString(new TranslationTextComponent("tooltip.plane.fuel", plane.getFuel()).getFormattedText(), xStart + 7, heightFunc.apply(5), 0);
        font.drawString(new TranslationTextComponent("tooltip.plane.damage", MathTools.round(plane.getPlaneDamage(), 2)).getFormattedText(), xStart + 7, heightFunc.apply(6), 0);

        RenderSystem.popMatrix();
    }

    private double cachedRelativeHeight = 0D;

    private double getRelativeHeight(EntityPlane plane) {
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
        if (player.getRidingEntity() instanceof EntityPlane) {
            EntityPlane plane = (EntityPlane) event.getPlayer().getRidingEntity();
            event.getMatrixStack().push();

            event.getMatrixStack().rotate(Vector3f.YP.rotationDegrees(-(plane.rotationYaw + (plane.rotationYaw - plane.prevRotationYaw) * event.getPartialRenderTick())));
            event.getMatrixStack().rotate(Vector3f.XP.rotationDegrees(plane.rotationPitch + (plane.rotationPitch - plane.prevRotationPitch) * event.getPartialRenderTick()));

            List<Entity> passengers = plane.getPassengers();
            int i = passengers.indexOf(player);
            if (i >= 0) {
                Vec3d offset = plane.getPlayerOffsets()[i];
                offset = offset.rotatePitch((float) -Math.toRadians(plane.rotationPitch));
                event.getMatrixStack().translate(0F, offset.y, 0F);
            }

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

    private EntityPlane getPlane() {
        if (mc.player.getRidingEntity() instanceof EntityPlane) {
            return (EntityPlane) mc.player.getRidingEntity();
        }
        return null;
    }
}
