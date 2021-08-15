package de.maxhenkel.plane.entity.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import de.maxhenkel.corelib.client.obj.OBJEntityRenderer;
import de.maxhenkel.plane.entity.EntityPlaneSoundBase;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public abstract class AbstractPlaneModel<T extends EntityPlaneSoundBase> extends OBJEntityRenderer<T> {

    protected AbstractPlaneModel(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    public void render(T plane, float yRot, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int light) {
        super.render(plane, yRot, partialTicks, matrixStack, buffer, light);
        if (plane.hasCustomName()) {
            String name = trimName(plane.getCustomName().getString(), 0.02F, 1F);
            drawName(plane, name, matrixStack, buffer, partialTicks, yRot, light, true);
            drawName(plane, name, matrixStack, buffer, partialTicks, yRot, light, false);
        }
    }

    protected String trimName(String name, float textScale, float maxLength) {
        while (getFont().width(name) * textScale > maxLength) {
            name = name.substring(0, name.length() - 1);
        }
        return name;
    }

    protected void drawName(T plane, String txt, PoseStack matrixStack, MultiBufferSource buffer, float partialTicks, float yRot, int light, boolean left) {
        matrixStack.pushPose();
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(-yRot));
        float xRot = plane.xRotO + (plane.getXRot() - plane.xRotO) * partialTicks;
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(xRot));
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        matrixStack.scale(1F, -1F, 1F);

        translateName(plane, matrixStack, left);

        int textWidth = getFont().width(txt);
        float textScale = 0.02F;

        matrixStack.translate(-(textScale * textWidth) / 2.0F, 0F, 0F);

        matrixStack.scale(textScale, textScale, textScale);

        getFont().drawInBatch(txt, 0F, 0F, 0xFFFFFF, false, matrixStack.last().pose(), buffer, false, 0, light);

        matrixStack.popPose();
    }

    protected abstract void translateName(T plane, PoseStack matrixStack, boolean left);

}