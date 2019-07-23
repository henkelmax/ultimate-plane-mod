package de.maxhenkel.plane.entity.render;

import de.maxhenkel.plane.entity.EntityPlanePart;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class PlanePartModel extends EntityRenderer<EntityPlanePart> {

    public PlanePartModel(EntityRendererManager rendererManager) {
        super(rendererManager);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityPlanePart entityPlanePart) {
        return null;
    }
}
