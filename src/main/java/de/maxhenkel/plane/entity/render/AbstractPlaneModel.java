package de.maxhenkel.plane.entity.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.maxhenkel.corelib.client.obj.OBJEntityRenderer;
import de.maxhenkel.plane.entity.EntityPlaneSoundBase;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.math.vector.Vector3f;

public abstract class AbstractPlaneModel<T extends EntityPlaneSoundBase> extends OBJEntityRenderer<T> {

    public AbstractPlaneModel(EntityRendererManager renderManager) {
        super(renderManager);
    }

    @Override
    public void render(T plane, float yaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int light) {
        super.render(plane, yaw, partialTicks, matrixStack, buffer, light);
        if (plane.hasCustomName()) {
            String name = trimName(plane.getCustomName().getString(), 0.02F, 1F);
            drawName(plane, name, matrixStack, buffer, yaw, light, true);
            drawName(plane, name, matrixStack, buffer, yaw, light, false);
        }
    }

    protected String trimName(String name, float textScale, float maxLength) {
        while (getFontRendererFromRenderManager().getStringWidth(name) * textScale > maxLength) {
            name = name.substring(0, name.length() - 1);
        }
        return name;
    }

    protected void drawName(T plane, String txt, MatrixStack matrixStack, IRenderTypeBuffer buffer, float yaw, int light, boolean left) {
        matrixStack.push();
        matrixStack.rotate(Vector3f.YP.rotationDegrees(-yaw));
        RenderSystem.color4f(1F, 1F, 1F, 1F);
        matrixStack.scale(1.0F, -1.0F, 1.0F);

        translateName(plane, matrixStack, left);

        int textWidth = getFontRendererFromRenderManager().getStringWidth(txt);
        float textScale = 0.02F;

        matrixStack.translate(-(textScale * textWidth) / 2.0F, 0F, 0F);

        matrixStack.scale(textScale, textScale, textScale);

        getFontRendererFromRenderManager().renderString(txt, 0F, 0F, 0xFFFFFF, false, matrixStack.getLast().getMatrix(), buffer, false, 0, light);

        matrixStack.pop();
    }

    protected abstract void translateName(T plane, MatrixStack matrixStack, boolean left);

}