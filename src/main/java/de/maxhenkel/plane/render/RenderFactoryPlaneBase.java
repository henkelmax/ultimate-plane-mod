package de.maxhenkel.plane.render;

import de.maxhenkel.plane.entity.plane.EntityPlaneBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderFactoryPlaneBase implements IRenderFactory<EntityPlaneBase> {

	@Override
	public Render<? super EntityPlaneBase> createRenderFor(RenderManager manager) {
		return new RenderPlaneBase(manager);
	}

}
