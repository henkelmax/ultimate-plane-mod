package de.maxhenkel.plane.item;

import de.maxhenkel.plane.Main;
import de.maxhenkel.plane.entity.EntityPlane;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ItemPlane extends ItemAbstractPlane<EntityPlane> {

    private EntityPlane.Type type;

    public ItemPlane(EntityPlane.Type type) {
        this.type = type;
        setRegistryName(new ResourceLocation(Main.MODID, "plane_" + type.getTypeName()));
    }

    @Override
    public EntityPlane createPlane(World world) {
        EntityPlane plane = new EntityPlane(world);
        plane.setPlaneType(type);
        return plane;
    }
}
