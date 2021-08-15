package de.maxhenkel.plane.item;

import de.maxhenkel.plane.Main;
import de.maxhenkel.plane.entity.EntityCargoPlane;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class ItemCargoPlane extends ItemAbstractPlane<EntityCargoPlane> {

    private EntityCargoPlane.Type type;

    public ItemCargoPlane(EntityCargoPlane.Type type) {
        this.type = type;
        setRegistryName(new ResourceLocation(Main.MODID, "cargo_plane_" + type.getTypeName()));
    }

    @Override
    public EntityCargoPlane createPlane(Level world) {
        EntityCargoPlane plane = new EntityCargoPlane(world);
        plane.setPlaneType(type);
        return plane;
    }
}
