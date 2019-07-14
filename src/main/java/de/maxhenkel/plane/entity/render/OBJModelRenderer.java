package de.maxhenkel.plane.entity.render;

import com.mojang.blaze3d.platform.GlStateManager;
import de.maxhenkel.plane.entity.EntityPlaneBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

import java.util.List;

// https://github.com/2piradians/Minewatch/tree/1.12.1/src/main/java/twopiradians/minewatch/client
public abstract class OBJModelRenderer<T extends EntityPlaneBase> extends EntityRenderer<T> {

    protected OBJModelRenderer(EntityRendererManager renderManager) {
        super(renderManager);
    }

    public abstract List<OBJModelInstance> getModels(T entity);

    @Override
    protected ResourceLocation getEntityTexture(T entity) {
        return null;
    }

    @Override
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
        List<OBJModelInstance> models = getModels(entity);

        GlStateManager.pushMatrix();
        setupTranslation(x, y, z);

        setupRotation(entity, entityYaw, partialTicks);

        GlStateManager.pushMatrix();

        //Render parts
        for (int i = 0; i < models.size(); i++) {
            Minecraft.getInstance().getTextureManager().bindTexture(models.get(i).getModel().getTexture());
            GlStateManager.pushMatrix();
            if (models.get(i).getModel().hasCulling()) {
                GlStateManager.enableCull();
            } else {
                GlStateManager.disableCull();
            }

            GlStateManager.translated(models.get(i).getOptions().getOffset().x, models.get(i).getOptions().getOffset().y, models.get(i).getOptions().getOffset().z);
            GlStateManager.rotatef(-90F, 1F, 0F, 0F);

            if (models.get(i).getOptions().getRotation() != null) {
                models.get(i).getOptions().getRotation().applyGLRotation();
            }

            if (models.get(i).getOptions().getOnRender() != null) {
                models.get(i).getOptions().getOnRender().onRender(partialTicks);
            }

            OBJRenderer.renderObj(models.get(i).getModel().getModel());
            GlStateManager.enableCull();
            GlStateManager.popMatrix();
        }

        GlStateManager.popMatrix();

        GlStateManager.popMatrix();

        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    public void setupRotation(T entity, float yaw, float partialTicks) {
        GlStateManager.rotatef(180.0F - yaw, 0F, 1F, 0F);

        float pitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;

        GlStateManager.rotatef(pitch, -1F, 0F, 0F);
    }

    public void setupTranslation(double x, double y, double z) {
        GlStateManager.translated(x, y, z);
    }

    @Override
    public boolean isMultipass() {
        return false;
    }


}