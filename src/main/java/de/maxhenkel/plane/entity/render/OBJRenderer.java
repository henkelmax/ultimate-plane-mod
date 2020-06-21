package de.maxhenkel.plane.entity.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.maxhenkel.plane.entity.EntityPlaneSoundBase;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public abstract class OBJRenderer<T extends EntityPlaneSoundBase> extends EntityRenderer<T> {

    protected OBJRenderer(EntityRendererManager renderManager) {
        super(renderManager);
    }

    public abstract List<OBJModelInstance> getModels(T entity);

    @Override
    public ResourceLocation getEntityTexture(T entity) {
        return null;
    }

    @Override
    public void render(T entity, float yaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int light) {
        List<OBJModelInstance> models = getModels(entity);

        matrixStack.push();

        matrixStack.rotate(Vector3f.YP.rotationDegrees(180F - yaw));
        float pitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
        matrixStack.rotate(Vector3f.XN.rotationDegrees(pitch));

        for (int i = 0; i < models.size(); i++) {
            matrixStack.push();

            matrixStack.translate(models.get(i).getOptions().getOffset().x, models.get(i).getOptions().getOffset().y, models.get(i).getOptions().getOffset().z);

            if (models.get(i).getOptions().getRotation() != null) {
                models.get(i).getOptions().getRotation().applyRotation(matrixStack);
            }

            if (models.get(i).getOptions().getOnRender() != null) {
                models.get(i).getOptions().getOnRender().onRender(entity, matrixStack, partialTicks);
            }

            models.get(i).getModel().render(models.get(i).getOptions().getTexture(), matrixStack, buffer, light);
            matrixStack.pop();
        }

        matrixStack.pop();

        super.render(entity, yaw, partialTicks, matrixStack, buffer, light);
    }

}