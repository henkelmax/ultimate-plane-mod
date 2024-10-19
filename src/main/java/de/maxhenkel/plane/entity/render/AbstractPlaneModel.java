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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public abstract class AbstractPlaneModel<T extends EntityPlaneBase> extends EntityRenderer<T, PlaneRenderState> {

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
    public void render(PlaneRenderState state, PoseStack pose, MultiBufferSource buffer, int light) {
        super.render(state, pose, buffer, light);
        pose.pushPose();
        pose.mulPose(Axis.YP.rotationDegrees(180F - state.yRot));
        Vec3 bodyCenter = state.bodyRotationCenter;
        pose.rotateAround(Axis.XN.rotationDegrees(state.xRot), (float) bodyCenter.x, (float) bodyCenter.y, (float) bodyCenter.z);

        pose.pushPose();
        Vector3f leftWheelOffset = getLeftWheelOffset(state);
        pose.translate(leftWheelOffset.x, leftWheelOffset.y, leftWheelOffset.z);
        pose.scale(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE);
        pose.mulPose(Axis.XP.rotationDegrees(-state.wheelRotation));
        WHEEL.render(WHEEL_TEXTURE, pose, buffer, light);
        pose.popPose();

        pose.pushPose();
        Vector3f rightWheelOffset = getRightWheelOffset(state);
        pose.translate(rightWheelOffset.x, rightWheelOffset.y, rightWheelOffset.z);
        pose.scale(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE);
        pose.mulPose(Axis.XP.rotationDegrees(-state.wheelRotation));
        WHEEL.render(WHEEL_TEXTURE, pose, buffer, light);
        pose.popPose();

        pose.pushPose();
        Vector3f propellerOffset = getPropellerOffset(state);
        pose.translate(propellerOffset.x, propellerOffset.y, propellerOffset.z);
        pose.scale(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE);
        pose.mulPose(Axis.ZP.rotationDegrees(-state.propellerRotation));
        PROPELLER.render(PROPELLER_TEXTURE, pose, buffer, light);
        pose.popPose();

        pose.pushPose();
        Vector3f bodyOffset = getBodyOffset(state);
        pose.translate(bodyOffset.x, bodyOffset.y, bodyOffset.z);
        pose.mulPose(Axis.YP.rotationDegrees(180F));
        getBodyModel(state).render(getBodyTexture(state), pose, buffer, light);
        pose.popPose();

        if (state.customName != null) {
            drawName(state, state.customName, pose, buffer, light, true);
            drawName(state, state.customName, pose, buffer, light, false);
        }
        pose.popPose();
    }

    @Override
    public PlaneRenderState createRenderState() {
        return new PlaneRenderState();
    }

    @Override
    public void extractRenderState(T plane, PlaneRenderState state, float partialTicks) {
        super.extractRenderState(plane, state, partialTicks);
        state.type = plane.getPlaneType();
        state.xRot = plane.xRotO + (plane.getXRot() - plane.xRotO) * state.partialTick;
        state.yRot = plane.yRotO + (plane.getYRot() - plane.yRotO) * state.partialTick;
        state.wheelRotation = plane.getWheelRotation(state.partialTick);
        state.propellerRotation = plane.getPropellerRotation(state.partialTick);
        state.bodyRotationCenter = plane.getBodyRotationCenter();
        state.customName = plane.getCustomName();
    }

    public static final float MAX_TEXT_SCALE = 0.02F;
    public static final float MAX_TEXT_WIDTH = 0.9F;

    protected void drawName(PlaneRenderState plane, Component name, PoseStack matrixStack, MultiBufferSource buffer, int light, boolean left) {
        matrixStack.pushPose();
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        matrixStack.scale(1F, -1F, 1F);

        translateName(plane, matrixStack, left);

        int textWidth = getFont().width(name);
        float textScale = Math.min(MAX_TEXT_SCALE, MAX_TEXT_WIDTH / textWidth);

        matrixStack.translate(-(textScale * textWidth) / 2F, 0F, 0F);

        matrixStack.scale(textScale, textScale, textScale);

        getFont().drawInBatch(name, 0F, 0F, 0xFFFFFF, false, matrixStack.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, light);

        matrixStack.popPose();
    }

    protected abstract void translateName(PlaneRenderState plane, PoseStack matrixStack, boolean left);

    protected abstract Vector3f getLeftWheelOffset(PlaneRenderState plane);

    protected abstract Vector3f getRightWheelOffset(PlaneRenderState plane);

    protected abstract Vector3f getPropellerOffset(PlaneRenderState plane);

    protected abstract Vector3f getBodyOffset(PlaneRenderState plane);

    protected abstract OBJModel getBodyModel(PlaneRenderState plane);

    protected abstract ResourceLocation getBodyTexture(PlaneRenderState plane);

    @Override
    protected boolean shouldShowName(T entity, double d) {
        return false;
    }
}