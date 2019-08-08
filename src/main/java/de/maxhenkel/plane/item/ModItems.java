package de.maxhenkel.plane.item;

import de.maxhenkel.plane.entity.EntityPlane;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ModItems {

    public static ItemPlane[] PLANES = Arrays.asList(EntityPlane.Type.values()).stream().map(ItemPlane::new).collect(Collectors.toList()).toArray(new ItemPlane[0]);
    public static ItemWrench WRENCH = new ItemWrench();


}
