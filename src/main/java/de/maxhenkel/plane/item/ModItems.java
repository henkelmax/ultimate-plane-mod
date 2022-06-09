package de.maxhenkel.plane.item;

import de.maxhenkel.plane.Main;
import de.maxhenkel.plane.entity.EntityBushPlane;
import de.maxhenkel.plane.entity.EntityCargoPlane;
import de.maxhenkel.plane.entity.EntityPlane;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ModItems {

    private static final DeferredRegister<Item> ITEM_REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, Main.MODID);

    public static final RegistryObject<ItemPlane>[] PLANES = Arrays.asList(EntityPlane.Type.values()).stream().map(type -> ITEM_REGISTER.register("plane_" + type.getTypeName(), () -> new ItemPlane(type))).collect(Collectors.toList()).toArray(new RegistryObject[0]);
    public static final RegistryObject<ItemCargoPlane>[] CARGO_PLANES = Arrays.asList(EntityCargoPlane.Type.values()).stream().map(type -> ITEM_REGISTER.register("cargo_plane_" + type.getTypeName(), () -> new ItemCargoPlane(type))).collect(Collectors.toList()).toArray(new RegistryObject[0]);
    public static final RegistryObject<ItemBushPlane>[] BUSH_PLANES = Arrays.asList(EntityBushPlane.Type.values()).stream().map(type -> ITEM_REGISTER.register("bush_plane_" + type.getTypeName(), () -> new ItemBushPlane(type))).collect(Collectors.toList()).toArray(new RegistryObject[0]);
    public static final RegistryObject<ItemWrench> WRENCH = ITEM_REGISTER.register("wrench", () -> new ItemWrench());
    public static final RegistryObject<ItemCraftingComponent> PLANE_WHEEL = ITEM_REGISTER.register("plane_wheel", () -> new ItemCraftingComponent());
    public static final RegistryObject<ItemCraftingComponent> PLANE_ENGINE = ITEM_REGISTER.register("plane_engine", () -> new ItemCraftingComponent());
    public static final RegistryObject<ItemCraftingComponent> DIAMOND_REINFORCED_IRON = ITEM_REGISTER.register("diamond_reinforced_iron", () -> new ItemCraftingComponent());

    public static void init() {
        ITEM_REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

}
