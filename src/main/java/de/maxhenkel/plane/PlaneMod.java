package de.maxhenkel.plane;

import de.maxhenkel.corelib.CommonRegistry;
import de.maxhenkel.plane.entity.*;
import de.maxhenkel.plane.events.InteractEvents;
import de.maxhenkel.plane.gui.ContainerPlane;
import de.maxhenkel.plane.item.ModItems;
import de.maxhenkel.plane.loottable.CopyPlaneData;
import de.maxhenkel.plane.net.MessageControlPlane;
import de.maxhenkel.plane.net.MessagePlaneGui;
import de.maxhenkel.plane.sound.ModSounds;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import javax.annotation.Nullable;
import java.util.UUID;

@Mod(PlaneMod.MODID)
@EventBusSubscriber(modid = PlaneMod.MODID)
public class PlaneMod {

    public static final String MODID = "plane";

    private static final DeferredRegister<LootItemFunctionType<?>> LOOT_FUNCTION_TYPE_REGISTER = DeferredRegister.create(BuiltInRegistries.LOOT_FUNCTION_TYPE, PlaneMod.MODID);
    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<CopyPlaneData>> COPY_PLANE_DATA = LOOT_FUNCTION_TYPE_REGISTER.register("copy_plane_data", () -> new LootItemFunctionType<>(CopyPlaneData.CODEC));

    public static ServerConfig SERVER_CONFIG;
    public static ClientConfig CLIENT_CONFIG;

    public PlaneMod(IEventBus eventBus) {
        SERVER_CONFIG = CommonRegistry.registerConfig(MODID, ModConfig.Type.SERVER, ServerConfig.class, true);
        CLIENT_CONFIG = CommonRegistry.registerConfig(MODID, ModConfig.Type.CLIENT, ClientConfig.class);

        ModItems.init(eventBus);
        ModSounds.init(eventBus);
        ModCreativeTabs.init(eventBus);
        ENTITY_REGISTER.register(eventBus);
        MENU_TYPE_REGISTER.register(eventBus);
        LOOT_FUNCTION_TYPE_REGISTER.register(eventBus);
    }

    @SubscribeEvent
    static void commonSetup(FMLCommonSetupEvent event) {
        NeoForge.EVENT_BUS.register(new InteractEvents());
    }

    @SubscribeEvent
    static void onRegisterPayloadHandler(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(MODID).versioned("0");
        CommonRegistry.registerMessage(registrar, MessageControlPlane.class);
        CommonRegistry.registerMessage(registrar, MessagePlaneGui.class);
    }

    private static final DeferredRegister<EntityType<?>> ENTITY_REGISTER = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, PlaneMod.MODID);
    public static final DeferredHolder<EntityType<?>, EntityType<EntityPlane>> PLANE_ENTITY_TYPE = ENTITY_REGISTER.register("plane", () -> {
        return CommonRegistry.registerEntity(PlaneMod.MODID, "plane", MobCategory.MISC, EntityPlane.class, builder -> {
            builder
                    .setTrackingRange(256)
                    .setUpdateInterval(1)
                    .setShouldReceiveVelocityUpdates(true)
                    .sized(3.5F, 2F)
                    .eyeHeight(1F);
        });
    });
    public static final DeferredHolder<EntityType<?>, EntityType<EntityCargoPlane>> CARGO_PLANE_ENTITY_TYPE = ENTITY_REGISTER.register("cargo_plane", () -> {
        return CommonRegistry.registerEntity(PlaneMod.MODID, "cargo_plane", MobCategory.MISC, EntityCargoPlane.class, builder -> {
            builder
                    .setTrackingRange(256)
                    .setUpdateInterval(1)
                    .setShouldReceiveVelocityUpdates(true)
                    .sized(3.5F, 2F)
                    .eyeHeight(1F);
        });
    });
    public static final DeferredHolder<EntityType<?>, EntityType<EntityTransporterPlane>> TRANSPORTER_PLANE_ENTITY_TYPE = ENTITY_REGISTER.register("transporter_plane", () -> {
        return CommonRegistry.registerEntity(PlaneMod.MODID, "transporter_plane", MobCategory.MISC, EntityTransporterPlane.class, builder -> {
            builder
                    .setTrackingRange(256)
                    .setUpdateInterval(1)
                    .setShouldReceiveVelocityUpdates(true)
                    .sized(3.5F, 2F)
                    .eyeHeight(1F);
        });
    });
    public static final DeferredHolder<EntityType<?>, EntityType<EntityBushPlane>> BUSH_PLANE_ENTITY_TYPE = ENTITY_REGISTER.register("bush_plane", () -> {
        return CommonRegistry.registerEntity(PlaneMod.MODID, "bush_plane", MobCategory.MISC, EntityBushPlane.class, builder -> {
            builder
                    .setTrackingRange(256)
                    .setUpdateInterval(1)
                    .setShouldReceiveVelocityUpdates(true)
                    .sized(3.5F, 2F)
                    .eyeHeight(1F);
        });
    });

    private static final DeferredRegister<MenuType<?>> MENU_TYPE_REGISTER = DeferredRegister.create(BuiltInRegistries.MENU, PlaneMod.MODID);
    public static DeferredHolder<MenuType<?>, MenuType<ContainerPlane>> PLANE_CONTAINER_TYPE = MENU_TYPE_REGISTER.register("plane", () -> {
        return new MenuType<>((IContainerFactory<ContainerPlane>) (windowId, inv, data) -> {
            EntityPlaneSoundBase plane = getPlaneByUUID(inv.player, data.readUUID());
            if (plane == null) {
                return null;
            }
            return new ContainerPlane(windowId, plane, inv);
        }, FeatureFlags.VANILLA_SET);
    });

    @Nullable
    public static EntityPlaneSoundBase getPlaneByUUID(Player player, UUID uuid) {
        double distance = 10D;
        return player.level().getEntitiesOfClass(EntityPlaneSoundBase.class, new AABB(player.getX() - distance, player.getY() - distance, player.getZ() - distance, player.getX() + distance, player.getY() + distance, player.getZ() + distance), entity -> entity.getUUID().equals(uuid)).stream().findAny().orElse(null);
    }

    @SubscribeEvent
    static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        registerEntityCapabilities(event, PLANE_ENTITY_TYPE);
        registerEntityCapabilities(event, BUSH_PLANE_ENTITY_TYPE);
        registerEntityCapabilities(event, CARGO_PLANE_ENTITY_TYPE);
        registerEntityCapabilities(event, TRANSPORTER_PLANE_ENTITY_TYPE);
    }

    private static <T extends EntityVehicleBase> void registerEntityCapabilities(RegisterCapabilitiesEvent event, DeferredHolder<EntityType<?>, EntityType<T>> holder) {
        event.registerEntity(Capabilities.FluidHandler.ENTITY, holder.get(), (object, context) -> {
            if (object instanceof IFluidHandler fluidHandler) {
                return fluidHandler;
            }
            return null;
        });
        event.registerEntity(Capabilities.EnergyStorage.ENTITY, holder.get(), (object, context) -> {
            if (object instanceof IEnergyStorage energyStorage) {
                return energyStorage;
            }
            return null;
        });
        event.registerEntity(Capabilities.ItemHandler.ENTITY, holder.get(), (object, context) -> {
            if (object instanceof IItemHandler itemHandler) {
                return itemHandler;
            }
            return null;
        });
    }

}
