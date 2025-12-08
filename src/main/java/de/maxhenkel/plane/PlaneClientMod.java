package de.maxhenkel.plane;

import de.maxhenkel.plane.entity.render.BushPlaneModel;
import de.maxhenkel.plane.entity.render.CargoPlaneModel;
import de.maxhenkel.plane.entity.render.PlaneModel;
import de.maxhenkel.plane.entity.render.TransporterPlaneModel;
import de.maxhenkel.plane.events.KeyEvents;
import de.maxhenkel.plane.events.RenderEvents;
import de.maxhenkel.plane.gui.ContainerPlane;
import de.maxhenkel.plane.gui.PlaneScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.lwjgl.glfw.GLFW;

@Mod(value = PlaneMod.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = PlaneMod.MODID, value = Dist.CLIENT)
public class PlaneClientMod {

    public static KeyMapping.Category KEY_CATEGORY_PLANE;
    public static KeyMapping PLANE_KEY;
    public static KeyMapping FORWARD_KEY;
    public static KeyMapping BACK_KEY;
    public static KeyMapping LEFT_KEY;
    public static KeyMapping RIGHT_KEY;
    public static KeyMapping UP_KEY;
    public static KeyMapping DOWN_KEY;
    public static KeyMapping START_KEY;
    public static KeyMapping BRAKE_KEY;

    @SubscribeEvent
    static void clientSetup(FMLClientSetupEvent event) {
        NeoForge.EVENT_BUS.register(new KeyEvents());
        NeoForge.EVENT_BUS.register(new RenderEvents());

        EntityRenderers.register(PlaneMod.PLANE_ENTITY_TYPE.get(), manager -> new PlaneModel(manager));
        EntityRenderers.register(PlaneMod.CARGO_PLANE_ENTITY_TYPE.get(), manager -> new CargoPlaneModel(manager));
        EntityRenderers.register(PlaneMod.TRANSPORTER_PLANE_ENTITY_TYPE.get(), manager -> new TransporterPlaneModel(manager));
        EntityRenderers.register(PlaneMod.BUSH_PLANE_ENTITY_TYPE.get(), manager -> new BushPlaneModel(manager));
    }

    @SubscribeEvent
    static void onRegisterKeyBinds(RegisterKeyMappingsEvent event) {
        KEY_CATEGORY_PLANE = new KeyMapping.Category(Identifier.fromNamespaceAndPath(PlaneMod.MODID, "plane"));
        event.registerCategory(KEY_CATEGORY_PLANE);
        PLANE_KEY = new KeyMapping("key.plane", GLFW.GLFW_KEY_P, KEY_CATEGORY_PLANE);
        FORWARD_KEY = new KeyMapping("key.plane_add_thrust", GLFW.GLFW_KEY_I, KEY_CATEGORY_PLANE);
        BACK_KEY = new KeyMapping("key.plane_remove_thrust", GLFW.GLFW_KEY_K, KEY_CATEGORY_PLANE);
        LEFT_KEY = new KeyMapping("key.plane_left", GLFW.GLFW_KEY_A, KEY_CATEGORY_PLANE);
        RIGHT_KEY = new KeyMapping("key.plane_right", GLFW.GLFW_KEY_D, KEY_CATEGORY_PLANE);
        UP_KEY = new KeyMapping("key.plane_up", GLFW.GLFW_KEY_S, KEY_CATEGORY_PLANE);
        DOWN_KEY = new KeyMapping("key.plane_down", GLFW.GLFW_KEY_W, KEY_CATEGORY_PLANE);
        START_KEY = new KeyMapping("key.plane_start", GLFW.GLFW_KEY_R, KEY_CATEGORY_PLANE);
        BRAKE_KEY = new KeyMapping("key.plane_brake", GLFW.GLFW_KEY_B, KEY_CATEGORY_PLANE);

        event.register(PLANE_KEY);
        event.register(FORWARD_KEY);
        event.register(BACK_KEY);
        event.register(LEFT_KEY);
        event.register(RIGHT_KEY);
        event.register(UP_KEY);
        event.register(DOWN_KEY);
        event.register(START_KEY);
        event.register(BRAKE_KEY);
    }

    @SubscribeEvent
    static void onRegisterScreens(RegisterMenuScreensEvent containers) {
        MenuScreens.ScreenConstructor factory = (MenuScreens.ScreenConstructor<ContainerPlane, PlaneScreen>) (container, playerInventory, name) -> new PlaneScreen(container, playerInventory, name);
        containers.register(PlaneMod.PLANE_CONTAINER_TYPE.get(), factory);

    }

}
