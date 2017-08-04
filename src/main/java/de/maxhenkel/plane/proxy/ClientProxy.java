package de.maxhenkel.plane.proxy;

import de.maxhenkel.plane.entity.plane.EntityPlaneBase;
import de.maxhenkel.plane.render.RenderFactoryPlaneBase;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

	public void preinit(FMLPreInitializationEvent event) {
		super.preinit(event);

		RenderingRegistry.registerEntityRenderingHandler(EntityPlaneBase.class, new RenderFactoryPlaneBase());
	}

	public void init(FMLInitializationEvent event) {
		super.init(event);
		
	}

	public void postinit(FMLPostInitializationEvent event) {
		super.postinit(event);
	}

}
