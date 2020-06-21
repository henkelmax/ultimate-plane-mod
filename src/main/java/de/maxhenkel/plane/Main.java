package de.maxhenkel.plane;

import de.maxhenkel.plane.entity.EntityCargoPlane;
import de.maxhenkel.plane.entity.EntityPlane;
import de.maxhenkel.plane.entity.EntityPlaneSoundBase;
import de.maxhenkel.plane.entity.render.CargoPlaneModel;
import de.maxhenkel.plane.entity.render.PlaneModel;
import de.maxhenkel.plane.events.CapabilityEvents;
import de.maxhenkel.plane.events.InteractEvents;
import de.maxhenkel.plane.events.KeyEvents;
import de.maxhenkel.plane.events.RenderEvents;
import de.maxhenkel.plane.gui.ContainerPlane;
import de.maxhenkel.plane.gui.GuiPlane;
import de.maxhenkel.plane.integration.waila.PluginPlane;
import de.maxhenkel.plane.item.ModItems;
import de.maxhenkel.plane.loottable.CopyPlaneData;
import de.maxhenkel.plane.net.MessageControlPlane;
import de.maxhenkel.plane.net.MessagePlaneGui;
import de.maxhenkel.plane.sound.ModSounds;
import net.minecraft.block.Block;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.fml.network.NetworkRegistry;
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

    public Main() {

        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Item.class, this::registerItems);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block.class, this::registerBlocks);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(SoundEvent.class, this::registerSounds);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(EntityType.class, this::registerEntities);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(ContainerType.class, this::registerContainers);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(TileEntityType.class, this::registerTileEntities);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(IRecipeSerializer.class, this::registerRecipes);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::configEvent);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_SPEC);

        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            clientStart();
        });
    }

    @OnlyIn(Dist.CLIENT)
    public void clientStart() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(Main.this::clientSetup);
    }

    @SubscribeEvent
    public void configEvent(ModConfig.ModConfigEvent event) {
        if (event.getConfig().getType() == ModConfig.Type.SERVER) {
            Config.loadServer();
        }
    }

    @SubscribeEvent
    public void commonSetup(FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new InteractEvents());
        MinecraftForge.EVENT_BUS.register(new CapabilityEvents());

        LootFunctionManager.registerFunction(new CopyPlaneData.Serializer());

        SIMPLE_CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(Main.MODID, "default"), () -> "1.0.0", s -> true, s -> true);
        SIMPLE_CHANNEL.registerMessage(0, MessageControlPlane.class, (msg, buf) -> msg.toBytes(buf), (buf) -> new MessageControlPlane().fromBytes(buf), (msg, fun) -> msg.executeServerSide(fun.get()));
        SIMPLE_CHANNEL.registerMessage(1, MessagePlaneGui.class, (msg, buf) -> msg.toBytes(buf), (buf) -> new MessagePlaneGui().fromBytes(buf), (msg, fun) -> msg.executeServerSide(fun.get()));
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

        ScreenManager.IScreenFactory factory = (ScreenManager.IScreenFactory<ContainerPlane, GuiPlane>) (container, playerInventory, name) -> new GuiPlane(container, playerInventory, name);
        ScreenManager.registerFactory(Main.PLANE_CONTAINER_TYPE, factory);

        PLANE_KEY = new KeyBinding("key.plane", GLFW.GLFW_KEY_P, "category.plane");
        ClientRegistry.registerKeyBinding(PLANE_KEY);

        FORWARD_KEY = new KeyBinding("key.plane_add_thrust", GLFW.GLFW_KEY_I, "category.plane");
        ClientRegistry.registerKeyBinding(FORWARD_KEY);

        BACK_KEY = new KeyBinding("key.plane_remove_thrust", GLFW.GLFW_KEY_K, "category.plane");
        ClientRegistry.registerKeyBinding(BACK_KEY);

        LEFT_KEY = new KeyBinding("key.plane_left", GLFW.GLFW_KEY_A, "category.plane");
        ClientRegistry.registerKeyBinding(LEFT_KEY);

        RIGHT_KEY = new KeyBinding("key.plane_right", GLFW.GLFW_KEY_D, "category.plane");
        ClientRegistry.registerKeyBinding(RIGHT_KEY);

        UP_KEY = new KeyBinding("key.plane_up", GLFW.GLFW_KEY_S, "category.plane");
        ClientRegistry.registerKeyBinding(UP_KEY);

        DOWN_KEY = new KeyBinding("key.plane_down", GLFW.GLFW_KEY_W, "category.plane");
        ClientRegistry.registerKeyBinding(DOWN_KEY);

        START_KEY = new KeyBinding("key.plane_start", GLFW.GLFW_KEY_R, "category.plane");
        ClientRegistry.registerKeyBinding(START_KEY);

        BRAKE_KEY = new KeyBinding("key.plane_brake", GLFW.GLFW_KEY_B, "category.plane");
        ClientRegistry.registerKeyBinding(BRAKE_KEY);

        MinecraftForge.EVENT_BUS.register(new KeyEvents());
        MinecraftForge.EVENT_BUS.register(new RenderEvents());
        try {
            Class.forName("mcp.mobius.waila.api.event.WailaRenderEvent");
            MinecraftForge.EVENT_BUS.register(new PluginPlane());
        } catch (ClassNotFoundException e) {
        }

        RenderingRegistry.registerEntityRenderingHandler(PLANE_ENTITY_TYPE, manager -> new PlaneModel(manager));
        RenderingRegistry.registerEntityRenderingHandler(CARGO_PLANE_ENTITY_TYPE, manager -> new CargoPlaneModel(manager));
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                ModItems.PLANES
        );
        event.getRegistry().registerAll(
                ModItems.CARGO_PLANES
        );
        event.getRegistry().registerAll(
                ModItems.WRENCH,
                ModItems.PLANE_WHEEL,
                ModItems.PLANE_ENGINE
        );
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(

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
        PLANE_ENTITY_TYPE = EntityType.Builder.<EntityPlane>create(EntityPlane::new, EntityClassification.MISC)
                .setTrackingRange(256)
                .setUpdateInterval(1)
                .setShouldReceiveVelocityUpdates(true)
                .size(3.5F, 2F)
                .setCustomClientFactory((spawnEntity, world) -> new EntityPlane(world))
                .build(Main.MODID + ":plane");
        PLANE_ENTITY_TYPE.setRegistryName(new ResourceLocation(Main.MODID, "plane"));
        event.getRegistry().register(PLANE_ENTITY_TYPE);

        CARGO_PLANE_ENTITY_TYPE = EntityType.Builder.<EntityCargoPlane>create(EntityCargoPlane::new, EntityClassification.MISC)
                .setTrackingRange(256)
                .setUpdateInterval(1)
                .setShouldReceiveVelocityUpdates(true)
                .size(3.5F, 2F)
                .setCustomClientFactory((spawnEntity, world) -> new EntityCargoPlane(world))
                .build(Main.MODID + ":cargo_plane");
        CARGO_PLANE_ENTITY_TYPE.setRegistryName(new ResourceLocation(Main.MODID, "cargo_plane"));
        event.getRegistry().register(CARGO_PLANE_ENTITY_TYPE);
    }

    public static ContainerType<ContainerPlane> PLANE_CONTAINER_TYPE;

    @SubscribeEvent
    public void registerContainers(RegistryEvent.Register<ContainerType<?>> event) {
        PLANE_CONTAINER_TYPE = new ContainerType<>((IContainerFactory<ContainerPlane>) (windowId, inv, data) -> {
            EntityPlaneSoundBase plane = getPlaneByUUID(inv.player, data.readUniqueId());
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
        return player.world.getEntitiesWithinAABB(EntityPlaneSoundBase.class, new AxisAlignedBB(player.getPosX() - distance, player.getPosY() - distance, player.getPosZ() - distance, player.getPosX() + distance, player.getPosY() + distance, player.getPosZ() + distance), entity -> entity.getUniqueID().equals(uuid)).stream().findAny().orElse(null);
    }

    @SubscribeEvent
    public void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event) {

    }

    @SubscribeEvent
    public void registerRecipes(RegistryEvent.Register<IRecipeSerializer<?>> event) {

    }

}
