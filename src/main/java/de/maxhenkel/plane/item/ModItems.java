package de.maxhenkel.plane.item;

import de.maxhenkel.plane.entity.EntityBushPlane;
import de.maxhenkel.plane.entity.EntityCargoPlane;
import de.maxhenkel.plane.entity.EntityPlane;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ModItems {

    public static ItemPlane[] PLANES = Arrays.asList(EntityPlane.Type.values()).stream().map(ItemPlane::new).collect(Collectors.toList()).toArray(new ItemPlane[0]);
    public static ItemCargoPlane[] CARGO_PLANES = Arrays.asList(EntityCargoPlane.Type.values()).stream().map(ItemCargoPlane::new).collect(Collectors.toList()).toArray(new ItemCargoPlane[0]);
    public static ItemBushPlane[] BUSH_PLANES = Arrays.asList(EntityBushPlane.Type.values()).stream().map(ItemBushPlane::new).collect(Collectors.toList()).toArray(new ItemBushPlane[0]);
    public static ItemWrench WRENCH = new ItemWrench();
    public static final ItemCraftingComponent PLANE_WHEEL = new ItemCraftingComponent("plane_wheel");
    public static final ItemCraftingComponent PLANE_ENGINE = new ItemCraftingComponent("plane_engine");
    public static final ItemCraftingComponent DIAMOND_REINFORCED_IRON = new ItemCraftingComponent("diamond_reinforced_iron");

}
