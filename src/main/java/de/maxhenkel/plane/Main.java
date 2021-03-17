package de.maxhenkel.plane;

import de.maxhenkel.corelib.ClientRegistry;
import de.maxhenkel.corelib.CommonRegistry;
import de.maxhenkel.plane.entity.EntityBushPlane;
import de.maxhenkel.plane.entity.EntityCargoPlane;
import de.maxhenkel.plane.entity.EntityPlane;
import de.maxhenkel.plane.entity.EntityPlaneSoundBase;
import de.maxhenkel.plane.entity.render.BushPlaneModel;
import de.maxhenkel.plane.entity.render.CargoPlaneModel;
import de.maxhenkel.plane.entity.render.PlaneModel;
import de.maxhenkel.plane.events.CapabilityEvents;
import de.maxhenkel.plane.events.InteractEvents;
import de.maxhenkel.plane.events.KeyEvents;
import de.maxhenkel.plane.events.RenderEvents;
import de.maxhenkel.plane.gui.ContainerPlane;
import de.maxhenkel.plane.gui.PlaneScreen;
import de.maxhenkel.plane.integration.waila.PluginPlane;
import de.maxhenkel.plane.item.ModItems;
import de.maxhenkel.plane.loottable.CopyPlaneData;
import de.maxhenkel.plane.net.MessageControlPlane;
import de.maxhenkel.plane.net.MessagePlaneGui;
import de.maxhenkel.plane.sound.ModSounds;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.UUID;

@Mod(Main.MODID)
public class Main {

    public static final String MODID = "plane";

    public static SimpleChannel SIMPLE_CHANNEL;

    public static EntityType<EntityPlane> PLANE_ENTITY_TYPE;
    public static EntityType<EntityCargoPlane> CARGO_PLANE_ENTITY_TYPE;
    public static EntityType<EntityBushPlane> BUSH_PLANE_ENTITY_TYPE;

    public static LootFunctionType COPY_PLANE_DATA;

    public static ServerConfig SERVER_CONFIG;
    public static ClientConfig CLIENT_CONFIG;

    public Main() {

        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Item.class, this::registerItems);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(SoundEvent.class, this::registerSounds);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(EntityType.class, this::registerEntities);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(ContainerType.class, this::registerContainers);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);

        SERVER_CONFIG = CommonRegistry.registerConfig(ModConfig.Type.SERVER, ServerConfig.class, true);
        CLIENT_CONFIG = CommonRegistry.registerConfig(ModConfig.Type.CLIENT, ClientConfig.class);

        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            clientStart();
        });
    }

    @OnlyIn(Dist.CLIENT)
    public void clientStart() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(Main.this::clientSetup);
    }

    @SubscribeEvent
    public void commonSetup(FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new InteractEvents());
        MinecraftForge.EVENT_BUS.register(new CapabilityEvents());

        COPY_PLANE_DATA = Registry.register(Registry.LOOT_FUNCTION_TYPE, new ResourceLocation(Main.MODID, "copy_plane_data"), new LootFunctionType(new CopyPlaneData.Serializer()));

        SIMPLE_CHANNEL = CommonRegistry.registerChannel(Main.MODID, "default");
        CommonRegistry.registerMessage(SIMPLE_CHANNEL, 0, MessageControlPlane.class);
        CommonRegistry.registerMessage(SIMPLE_CHANNEL, 1, MessagePlaneGui.class);
    }

    public static KeyBinding PLANE_KEY;
    public static KeyBinding FORWARD_KEY;
    public static KeyBinding BACK_KEY;
    public static KeyBinding LEFT_KEY;
    public static KeyBinding RIGHT_KEY;
    public static KeyBinding UP_KEY;
    public static KeyBinding DOWN_KEY;
    public static KeyBinding START_KEY;
    public static KeyBinding BRAKE_KEY;

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void clientSetup(FMLClientSetupEvent event) {

        ScreenManager.IScreenFactory factory = (ScreenManager.IScreenFactory<ContainerPlane, PlaneScreen>) (container, playerInventory, name) -> new PlaneScreen(container, playerInventory, name);
        ScreenManager.register(Main.PLANE_CONTAINER_TYPE, factory);

        PLANE_KEY = ClientRegistry.registerKeyBinding("key.plane", "category.plane", GLFW.GLFW_KEY_P);
        FORWARD_KEY = ClientRegistry.registerKeyBinding("key.plane_add_thrust", "category.plane", GLFW.GLFW_KEY_I);
        BACK_KEY = ClientRegistry.registerKeyBinding("key.plane_remove_thrust", "category.plane", GLFW.GLFW_KEY_K);
        LEFT_KEY = ClientRegistry.registerKeyBinding("key.plane_left", "category.plane", GLFW.GLFW_KEY_A);
        RIGHT_KEY = ClientRegistry.registerKeyBinding("key.plane_right", "category.plane", GLFW.GLFW_KEY_D);
        UP_KEY = ClientRegistry.registerKeyBinding("key.plane_up", "category.plane", GLFW.GLFW_KEY_S);
        DOWN_KEY = ClientRegistry.registerKeyBinding("key.plane_down", "category.plane", GLFW.GLFW_KEY_W);
        START_KEY = ClientRegistry.registerKeyBinding("key.plane_start", "category.plane", GLFW.GLFW_KEY_R);
        BRAKE_KEY = ClientRegistry.registerKeyBinding("key.plane_brake", "category.plane", GLFW.GLFW_KEY_B);

        MinecraftForge.EVENT_BUS.register(new KeyEvents());
        MinecraftForge.EVENT_BUS.register(new RenderEvents());
        try {
            Class.forName("mcp.mobius.waila.api.event.WailaRenderEvent");
            MinecraftForge.EVENT_BUS.register(new PluginPlane());
        } catch (ClassNotFoundException e) {
        }

        RenderingRegistry.registerEntityRenderingHandler(PLANE_ENTITY_TYPE, manager -> new PlaneModel(manager));
        RenderingRegistry.registerEntityRenderingHandler(CARGO_PLANE_ENTITY_TYPE, manager -> new CargoPlaneModel(manager));
        RenderingRegistry.registerEntityRenderingHandler(BUSH_PLANE_ENTITY_TYPE, manager -> new BushPlaneModel(manager));
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(ModItems.PLANES);
        event.getRegistry().registerAll(ModItems.CARGO_PLANES);
        event.getRegistry().registerAll(ModItems.BUSH_PLANES);
        event.getRegistry().registerAll(
                ModItems.WRENCH,
                ModItems.PLANE_WHEEL,
                ModItems.PLANE_ENGINE
        );
    }

    @SubscribeEvent
    public void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        event.getRegistry().registerAll(
                ModSounds.getAll().toArray(new SoundEvent[0])
        );
    }

    @SubscribeEvent
    public void registerEntities(RegistryEvent.Register<EntityType<?>> event) {
        PLANE_ENTITY_TYPE = CommonRegistry.registerEntity(Main.MODID, "plane", EntityClassification.MISC, EntityPlane.class, builder -> {
            builder
                    .setTrackingRange(256)
                    .setUpdateInterval(1)
                    .setShouldReceiveVelocityUpdates(true)
                    .sized(3.5F, 2F)
                    .setCustomClientFactory((spawnEntity, world) -> new EntityPlane(world));
        });
        event.getRegistry().register(PLANE_ENTITY_TYPE);

        CARGO_PLANE_ENTITY_TYPE = CommonRegistry.registerEntity(Main.MODID, "cargo_plane", EntityClassification.MISC, EntityCargoPlane.class, builder -> {
            builder
                    .setTrackingRange(256)
                    .setUpdateInterval(1)
                    .setShouldReceiveVelocityUpdates(true)
                    .sized(3.5F, 2F)
                    .setCustomClientFactory((spawnEntity, world) -> new EntityCargoPlane(world));
        });
        event.getRegistry().register(CARGO_PLANE_ENTITY_TYPE);

        BUSH_PLANE_ENTITY_TYPE = CommonRegistry.registerEntity(Main.MODID, "bush_plane", EntityClassification.MISC, EntityBushPlane.class, builder -> {
            builder
                    .setTrackingRange(256)
                    .setUpdateInterval(1)
                    .setShouldReceiveVelocityUpdates(true)
                    .sized(3.5F, 2F)
                    .setCustomClientFactory((spawnEntity, world) -> new EntityBushPlane(world));
        });
        event.getRegistry().register(BUSH_PLANE_ENTITY_TYPE);
    }

    public static ContainerType<ContainerPlane> PLANE_CONTAINER_TYPE;

    @SubscribeEvent
    public void registerContainers(RegistryEvent.Register<ContainerType<?>> event) {
        PLANE_CONTAINER_TYPE = new ContainerType<>((IContainerFactory<ContainerPlane>) (windowId, inv, data) -> {
            EntityPlaneSoundBase plane = getPlaneByUUID(inv.player, data.readUUID());
            if (plane == null) {
                return null;
            }
            return new ContainerPlane(windowId, plane, inv);
        });
        PLANE_CONTAINER_TYPE.setRegistryName(new ResourceLocation(Main.MODID, "plane"));
        event.getRegistry().register(PLANE_CONTAINER_TYPE);
    }

    @Nullable
    public static EntityPlaneSoundBase getPlaneByUUID(PlayerEntity player, UUID uuid) {
        double distance = 10D;
        return player.level.getEntitiesOfClass(EntityPlaneSoundBase.class, new AxisAlignedBB(player.getX() - distance, player.getY() - distance, player.getZ() - distance, player.getX() + distance, player.getY() + distance, player.getZ() + distance), entity -> entity.getUUID().equals(uuid)).stream().findAny().orElse(null);
    }

}
