package de.maxhenkel.plane.entity.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.maxhenkel.corelib.client.obj.OBJModel;
import de.maxhenkel.plane.Main;
import de.maxhenkel.plane.entity.EntityPlaneBase;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public abstract class AbstractPlaneModel<T extends EntityPlaneBase> extends EntityRenderer<T> {

    protected static final OBJModel WHEEL = new OBJModel(ResourceLocation.fromNamespaceAndPath(Main.MODID, "models/entity/wheel.obj"));
    protected static final OBJModel PROPELLER = new OBJModel(ResourceLocation.fromNamespaceAndPath(Main.MODID, "models/entity/propeller.obj"));

    protected static final ResourceLocation WHEEL_TEXTURE = ResourceLocation.fromNamespaceAndPath(Main.MODID, "textures/entity/wheel.png");
    //TODO Rework
    protected static final ResourceLocation PROPELLER_TEXTURE = ResourceLocation.withDefaultNamespace("textures/block/spruce_planks.png");

    protected static final ResourceLocation OAK_MODEL = ResourceLocation.withDefaultNamespace("textures/block/oak_planks.png");
    protected static final ResourceLocation DARK_OAK_MODEL = ResourceLocation.withDefaultNamespace("textures/block/dark_oak_planks.png");
    protected static final ResourceLocation BIRCH_MODEL = ResourceLocation.withDefaultNamespace("textures/block/birch_planks.png");
    protected static final ResourceLocation JUNGLE_MODEL = ResourceLocation.withDefaultNamespace("textures/block/jungle_planks.png");
    protected static final ResourceLocation ACACIA_MODEL = ResourceLocation.withDefaultNamespace("textures/block/acacia_planks.png");
    protected static final ResourceLocation SPRUCE_MODEL = ResourceLocation.withDefaultNamespace("textures/block/spruce_planks.png");
    protected static final ResourceLocation WARPED_MODEL = ResourceLocation.withDefaultNamespace("textures/block/warped_planks.png");
    protected static final ResourceLocation CRIMSON_MODEL = ResourceLocation.withDefaultNamespace("textures/block/crimson_planks.png");
    protected static final ResourceLocation BAMBOO_MODEL = ResourceLocation.withDefaultNamespace("textures/block/bamboo_planks.png");
    protected static final ResourceLocation CHERRY_MODEL = ResourceLocation.withDefaultNamespace("textures/block/cherry_planks.png");
    protected static final ResourceLocation MANGROVE_MODEL = ResourceLocation.withDefaultNamespace("textures/block/mangrove_planks.png");

    protected static final float MODEL_SCALE = 1F / 16F;

    protected AbstractPlaneModel(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    public void render(T plane, float yRot, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int light) {
        super.render(plane, yRot, partialTicks, poseStack, buffer, light);

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180F - yRot));
        float pitch = plane.xRotO + (plane.getXRot() - plane.xRotO) * partialTicks;
        Vec3 bodyCenter = plane.getBodyRotationCenter();
        poseStack.rotateAround(Axis.XN.rotationDegrees(pitch), (float) bodyCenter.x, (float) bodyCenter.y, (float) bodyCenter.z);

        poseStack.pushPose();
        Vector3f leftWheelOffset = getLeftWheelOffset(plane);
        poseStack.translate(leftWheelOffset.x, leftWheelOffset.y, leftWheelOffset.z);
        poseStack.scale(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE);
        poseStack.mulPose(Axis.XP.rotationDegrees(-plane.getWheelRotation(partialTicks)));
        WHEEL.render(WHEEL_TEXTURE, poseStack, buffer, light);
        poseStack.popPose();

        poseStack.pushPose();
        Vector3f rightWheelOffset = getRightWheelOffset(plane);
        poseStack.translate(rightWheelOffset.x, rightWheelOffset.y, rightWheelOffset.z);
        poseStack.scale(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE);
        poseStack.mulPose(Axis.XP.rotationDegrees(-plane.getWheelRotation(partialTicks)));
        WHEEL.render(WHEEL_TEXTURE, poseStack, buffer, light);
        poseStack.popPose();

        poseStack.pushPose();
        Vector3f propellerOffset = getPropellerOffset(plane);
        poseStack.translate(propellerOffset.x, propellerOffset.y, propellerOffset.z);
        poseStack.scale(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE);
        poseStack.mulPose(Axis.ZP.rotationDegrees(-plane.getPropellerRotation(partialTicks)));
        PROPELLER.render(PROPELLER_TEXTURE, poseStack, buffer, light);
        poseStack.popPose();

        poseStack.pushPose();
        Vector3f bodyOffset = getBodyOffset(plane);
        poseStack.translate(bodyOffset.x, bodyOffset.y, bodyOffset.z);
        poseStack.mulPose(Axis.YP.rotationDegrees(180F));
        getBodyModel(plane).render(getBodyTexture(plane), poseStack, buffer, light);
        poseStack.popPose();

        if (plane.hasCustomName()) {
            String name = trimName(plane.getCustomName().getString(), 0.02F, 1F);
            drawName(plane, name, poseStack, buffer, partialTicks, yRot, light, true);
            drawName(plane, name, poseStack, buffer, partialTicks, yRot, light, false);
        }
        poseStack.popPose();
    }

    protected String trimName(String name, float textScale, float maxLength) {
        while (getFont().width(name) * textScale > maxLength) {
            name = name.substring(0, name.length() - 1);
        }
        return name;
    }

    protected void drawName(T plane, String txt, PoseStack matrixStack, MultiBufferSource buffer, float partialTicks, float yRot, int light, boolean left) {
        matrixStack.pushPose();
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        matrixStack.scale(1F, -1F, 1F);

        translateName(plane, matrixStack, left);

        int textWidth = getFont().width(txt);
        float textScale = 0.02F;

        matrixStack.translate(-(textScale * textWidth) / 2F, 0F, 0F);

        matrixStack.scale(textScale, textScale, textScale);

        getFont().drawInBatch(txt, 0F, 0F, 0xFFFFFF, false, matrixStack.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, light);

        matrixStack.popPose();
    }

    protected abstract void translateName(T plane, PoseStack matrixStack, boolean left);

    protected abstract Vector3f getLeftWheelOffset(T plane);

    protected abstract Vector3f getRightWheelOffset(T plane);

    protected abstract Vector3f getPropellerOffset(T plane);

    protected abstract Vector3f getBodyOffset(T plane);

    protected abstract OBJModel getBodyModel(T plane);

    protected abstract ResourceLocation getBodyTexture(T plane);

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return null;
    }
}