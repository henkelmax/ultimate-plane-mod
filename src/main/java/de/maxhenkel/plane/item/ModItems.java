package de.maxhenkel.plane.item;

import de.maxhenkel.plane.Main;
import de.maxhenkel.plane.entity.EntityBushPlane;
import de.maxhenkel.plane.entity.EntityCargoPlane;
import de.maxhenkel.plane.entity.EntityPlane;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ModItems {

    private static final DeferredRegister<Item> ITEM_REGISTER = DeferredRegister.create(BuiltInRegistries.ITEM, Main.MODID);

    public static final DeferredHolder<Item, ItemPlane>[] PLANES = Arrays.asList(EntityPlane.Type.values()).stream().map(type -> ITEM_REGISTER.register("plane_" + type.getTypeName(), () -> new ItemPlane(type))).collect(Collectors.toList()).toArray(new DeferredHolder[0]);
    public static final DeferredHolder<Item, ItemCargoPlane>[] CARGO_PLANES = Arrays.asList(EntityCargoPlane.Type.values()).stream().map(type -> ITEM_REGISTER.register("cargo_plane_" + type.getTypeName(), () -> new ItemCargoPlane(type))).collect(Collectors.toList()).toArray(new DeferredHolder[0]);
    public static final DeferredHolder<Item, ItemBushPlane>[] BUSH_PLANES = Arrays.asList(EntityBushPlane.Type.values()).stream().map(type -> ITEM_REGISTER.register("bush_plane_" + type.getTypeName(), () -> new ItemBushPlane(type))).collect(Collectors.toList()).toArray(new DeferredHolder[0]);
    public static final DeferredHolder<Item, ItemWrench> WRENCH = ITEM_REGISTER.register("wrench", () -> new ItemWrench());
    public static final DeferredHolder<Item, ItemCraftingComponent> PLANE_WHEEL = ITEM_REGISTER.register("plane_wheel", () -> new ItemCraftingComponent());
    public static final DeferredHolder<Item, ItemCraftingComponent> PLANE_ENGINE = ITEM_REGISTER.register("plane_engine", () -> new ItemCraftingComponent());
    public static final DeferredHolder<Item, ItemCraftingComponent> DIAMOND_REINFORCED_IRON = ITEM_REGISTER.register("diamond_reinforced_iron", () -> new ItemCraftingComponent());

    public static void init() {
        ITEM_REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

}
