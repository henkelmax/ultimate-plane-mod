package de.maxhenkel.plane.proxy;

import de.maxhenkel.plane.Main;
import de.maxhenkel.plane.entity.plane.EntityPlaneBase;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class CommonProxy {

	public CommonProxy() {
		
	}
	
	public void preinit(FMLPreInitializationEvent event) {
		
	}
	
	public void init(FMLInitializationEvent event) {
		EntityRegistry.registerModEntity(EntityPlaneBase.class,
				"plane_base", 384446, Main.instance(), 64, 1, true);
	}

	public void postinit(FMLPostInitializationEvent event) {

	}

}
