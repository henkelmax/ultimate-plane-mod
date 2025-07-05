package de.maxhenkel.plane.item;

import de.maxhenkel.plane.PlaneMod;
import de.maxhenkel.plane.PlaneType;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Arrays;

public class ModItems {

    private static final DeferredRegister.Items ITEM_REGISTER = DeferredRegister.createItems(PlaneMod.MODID);

    public static final DeferredHolder<Item, ItemPlane>[] PLANES = Arrays.asList(PlaneType.values()).stream().map(type -> ITEM_REGISTER.registerItem("plane_" + type.getTypeName(), p -> new ItemPlane(type, p))).toList().toArray(new DeferredHolder[0]);
    public static final DeferredHolder<Item, ItemCargoPlane>[] CARGO_PLANES = Arrays.asList(PlaneType.values()).stream().map(type -> ITEM_REGISTER.registerItem("cargo_plane_" + type.getTypeName(), p -> new ItemCargoPlane(type, p))).toList().toArray(new DeferredHolder[0]);
    public static final DeferredHolder<Item, ItemTransporterPlane>[] TRANSPORTER_PLANES = Arrays.asList(PlaneType.values()).stream().map(type -> ITEM_REGISTER.registerItem("transporter_plane_" + type.getTypeName(), p -> new ItemTransporterPlane(type, p))).toList().toArray(new DeferredHolder[0]);
    public static final DeferredHolder<Item, ItemBushPlane>[] BUSH_PLANES = Arrays.asList(PlaneType.values()).stream().map(type -> ITEM_REGISTER.registerItem("bush_plane_" + type.getTypeName(), p -> new ItemBushPlane(type, p))).toList().toArray(new DeferredHolder[0]);
    public static final DeferredHolder<Item, ItemWrench> WRENCH = ITEM_REGISTER.registerItem("wrench", ItemWrench::new);
    public static final DeferredHolder<Item, ItemCraftingComponent> PLANE_WHEEL = ITEM_REGISTER.registerItem("plane_wheel", ItemCraftingComponent::new);
    public static final DeferredHolder<Item, ItemCraftingComponent> PLANE_ENGINE = ITEM_REGISTER.registerItem("plane_engine", ItemCraftingComponent::new);
    public static final DeferredHolder<Item, ItemCraftingComponent> DIAMOND_REINFORCED_IRON = ITEM_REGISTER.registerItem("diamond_reinforced_iron", ItemCraftingComponent::new);

    private static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPE_REGISTER = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, PlaneMod.MODID);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<PlaneData>> PLANE_DATA_COMPONENT = DATA_COMPONENT_TYPE_REGISTER.register("plane_data", () -> DataComponentType.<PlaneData>builder().persistent(PlaneData.CODEC).networkSynchronized(PlaneData.STREAM_CODEC).build());

    public static void init(IEventBus eventBus) {
        ITEM_REGISTER.register(eventBus);
        DATA_COMPONENT_TYPE_REGISTER.register(eventBus);
    }

}
