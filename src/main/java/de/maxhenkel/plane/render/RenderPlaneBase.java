package de.maxhenkel.plane.render;

import de.maxhenkel.plane.entity.plane.EntityPlaneBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderPlaneBase extends Render<EntityPlaneBase>{

	protected RenderPlaneBase(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityPlaneBase entity) {
		return null;
	}

}
