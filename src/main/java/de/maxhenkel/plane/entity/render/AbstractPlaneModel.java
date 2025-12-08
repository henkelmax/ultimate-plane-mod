package de.maxhenkel.plane.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.maxhenkel.corelib.client.obj.OBJModel;
import de.maxhenkel.plane.PlaneMod;
import de.maxhenkel.plane.entity.EntityPlaneBase;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public abstract class AbstractPlaneModel<T extends EntityPlaneBase> extends EntityRenderer<T, PlaneRenderState> {

    protected static final OBJModel WHEEL = new OBJModel(Identifier.fromNamespaceAndPath(PlaneMod.MODID, "models/entity/wheel.obj"));
    protected static final OBJModel PROPELLER = new OBJModel(Identifier.fromNamespaceAndPath(PlaneMod.MODID, "models/entity/propeller.obj"));

    protected static final Identifier WHEEL_TEXTURE = Identifier.fromNamespaceAndPath(PlaneMod.MODID, "textures/entity/wheel.png");
    //TODO Rework
    protected static final Identifier PROPELLER_TEXTURE = Identifier.withDefaultNamespace("textures/block/spruce_planks.png");

    protected static final Identifier OAK_MODEL = Identifier.withDefaultNamespace("textures/block/oak_planks.png");
    protected static final Identifier DARK_OAK_MODEL = Identifier.withDefaultNamespace("textures/block/dark_oak_planks.png");
    protected static final Identifier BIRCH_MODEL = Identifier.withDefaultNamespace("textures/block/birch_planks.png");
    protected static final Identifier JUNGLE_MODEL = Identifier.withDefaultNamespace("textures/block/jungle_planks.png");
    protected static final Identifier ACACIA_MODEL = Identifier.withDefaultNamespace("textures/block/acacia_planks.png");
    protected static final Identifier SPRUCE_MODEL = Identifier.withDefaultNamespace("textures/block/spruce_planks.png");
    protected static final Identifier WARPED_MODEL = Identifier.withDefaultNamespace("textures/block/warped_planks.png");
    protected static final Identifier CRIMSON_MODEL = Identifier.withDefaultNamespace("textures/block/crimson_planks.png");
    protected static final Identifier BAMBOO_MODEL = Identifier.withDefaultNamespace("textures/block/bamboo_planks.png");
    protected static final Identifier CHERRY_MODEL = Identifier.withDefaultNamespace("textures/block/cherry_planks.png");
    protected static final Identifier MANGROVE_MODEL = Identifier.withDefaultNamespace("textures/block/mangrove_planks.png");

    protected static final float MODEL_SCALE = 1F / 16F;

    protected AbstractPlaneModel(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    public void submit(PlaneRenderState state, PoseStack stack, SubmitNodeCollector collector, CameraRenderState cameraRenderState) {
        super.submit(state, stack, collector, cameraRenderState);
        stack.pushPose();
        stack.mulPose(Axis.YP.rotationDegrees(180F - state.yRot));
        Vec3 bodyCenter = state.bodyRotationCenter;
        stack.rotateAround(Axis.XN.rotationDegrees(state.xRot), (float) bodyCenter.x, (float) bodyCenter.y, (float) bodyCenter.z);

        stack.pushPose();
        Vector3f leftWheelOffset = getLeftWheelOffset(state);
        stack.translate(leftWheelOffset.x, leftWheelOffset.y, leftWheelOffset.z);
        stack.scale(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE);
        stack.mulPose(Axis.XP.rotationDegrees(-state.wheelRotation));
        WHEEL.submitModels(WHEEL_TEXTURE, stack, collector, cameraRenderState, state.lightCoords);
        stack.popPose();

        stack.pushPose();
        Vector3f rightWheelOffset = getRightWheelOffset(state);
        stack.translate(rightWheelOffset.x, rightWheelOffset.y, rightWheelOffset.z);
        stack.scale(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE);
        stack.mulPose(Axis.XP.rotationDegrees(-state.wheelRotation));
        WHEEL.submitModels(WHEEL_TEXTURE, stack, collector, cameraRenderState, state.lightCoords);
        stack.popPose();

        stack.pushPose();
        Vector3f propellerOffset = getPropellerOffset(state);
        stack.translate(propellerOffset.x, propellerOffset.y, propellerOffset.z);
        stack.scale(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE);
        stack.mulPose(Axis.ZP.rotationDegrees(-state.propellerRotation));
        PROPELLER.submitModels(PROPELLER_TEXTURE, stack, collector, cameraRenderState, state.lightCoords);
        stack.popPose();

        stack.pushPose();
        Vector3f bodyOffset = getBodyOffset(state);
        stack.translate(bodyOffset.x, bodyOffset.y, bodyOffset.z);
        stack.mulPose(Axis.YP.rotationDegrees(180F));
        getBodyModel(state).submitModels(getBodyTexture(state), stack, collector, cameraRenderState, state.lightCoords);
        stack.popPose();

        if (state.customName != null) {
            drawName(state, state.customName, stack, collector, state.lightCoords, true);
            drawName(state, state.customName, stack, collector, state.lightCoords, false);
        }
        stack.popPose();
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
        Component customName = plane.getCustomName();
        state.customName = customName == null ? null : customName.getVisualOrderText();
    }

    public static final float MAX_TEXT_SCALE = 0.02F;
    public static final float MAX_TEXT_WIDTH = 0.9F;

    protected void drawName(PlaneRenderState plane, FormattedCharSequence name, PoseStack stack, SubmitNodeCollector collector, int light, boolean left) {
        stack.pushPose();
        stack.scale(1F, -1F, 1F);

        translateName(plane, stack, left);

        int textWidth = getFont().width(name);
        float textScale = Math.min(MAX_TEXT_SCALE, MAX_TEXT_WIDTH / textWidth);

        stack.translate(-(textScale * textWidth) / 2F, 0F, 0F);

        stack.scale(textScale, textScale, textScale);

        collector.submitText(stack, 0F, 0F, name, false, Font.DisplayMode.NORMAL, light, 0xFFFFFFFF, 0, 0);

        stack.popPose();
    }

    protected abstract void translateName(PlaneRenderState plane, PoseStack matrixStack, boolean left);

    protected abstract Vector3f getLeftWheelOffset(PlaneRenderState plane);

    protected abstract Vector3f getRightWheelOffset(PlaneRenderState plane);

    protected abstract Vector3f getPropellerOffset(PlaneRenderState plane);

    protected abstract Vector3f getBodyOffset(PlaneRenderState plane);

    protected abstract OBJModel getBodyModel(PlaneRenderState plane);

    protected abstract Identifier getBodyTexture(PlaneRenderState plane);

    @Override
    protected boolean shouldShowName(T entity, double d) {
        return false;
    }
}