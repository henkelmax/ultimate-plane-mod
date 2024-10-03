package de.maxhenkel.plane.item;

import de.maxhenkel.plane.Main;
import de.maxhenkel.plane.PlaneType;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Arrays;

public class ModItems {

    private static final DeferredRegister<Item> ITEM_REGISTER = DeferredRegister.create(BuiltInRegistries.ITEM, Main.MODID);

    public static final DeferredHolder<Item, ItemPlane>[] PLANES = Arrays.asList(PlaneType.values()).stream().map(type -> ITEM_REGISTER.register("plane_" + type.getTypeName(), () -> new ItemPlane(type))).toList().toArray(new DeferredHolder[0]);
    public static final DeferredHolder<Item, ItemCargoPlane>[] CARGO_PLANES = Arrays.asList(PlaneType.values()).stream().map(type -> ITEM_REGISTER.register("cargo_plane_" + type.getTypeName(), () -> new ItemCargoPlane(type))).toList().toArray(new DeferredHolder[0]);
    public static final DeferredHolder<Item, ItemTransporterPlane>[] TRANSPORTER_PLANES = Arrays.asList(PlaneType.values()).stream().map(type -> ITEM_REGISTER.register("transporter_plane_" + type.getTypeName(), () -> new ItemTransporterPlane(type))).toList().toArray(new DeferredHolder[0]);
    public static final DeferredHolder<Item, ItemBushPlane>[] BUSH_PLANES = Arrays.asList(PlaneType.values()).stream().map(type -> ITEM_REGISTER.register("bush_plane_" + type.getTypeName(), () -> new ItemBushPlane(type))).toList().toArray(new DeferredHolder[0]);
    public static final DeferredHolder<Item, ItemWrench> WRENCH = ITEM_REGISTER.register("wrench", () -> new ItemWrench());
    public static final DeferredHolder<Item, ItemCraftingComponent> PLANE_WHEEL = ITEM_REGISTER.register("plane_wheel", () -> new ItemCraftingComponent());
    public static final DeferredHolder<Item, ItemCraftingComponent> PLANE_ENGINE = ITEM_REGISTER.register("plane_engine", () -> new ItemCraftingComponent());
    public static final DeferredHolder<Item, ItemCraftingComponent> DIAMOND_REINFORCED_IRON = ITEM_REGISTER.register("diamond_reinforced_iron", () -> new ItemCraftingComponent());

    private static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPE_REGISTER = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, Main.MODID);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<PlaneData>> PLANE_DATA_COMPONENT = DATA_COMPONENT_TYPE_REGISTER.register("plane_data", () -> DataComponentType.<PlaneData>builder().persistent(PlaneData.CODEC).networkSynchronized(PlaneData.STREAM_CODEC).build());

    public static void init(IEventBus eventBus) {
        ITEM_REGISTER.register(eventBus);
        DATA_COMPONENT_TYPE_REGISTER.register(eventBus);
    }

}
