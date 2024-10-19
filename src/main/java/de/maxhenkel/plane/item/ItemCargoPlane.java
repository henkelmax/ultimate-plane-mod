package de.maxhenkel.plane.item;

import de.maxhenkel.plane.PlaneType;
import de.maxhenkel.plane.entity.EntityCargoPlane;
import net.minecraft.world.level.Level;

public class ItemCargoPlane extends ItemAbstractPlane<EntityCargoPlane> {

    public ItemCargoPlane(PlaneType type, Properties properties) {
        super(type, properties);
    }

    @Override
    public EntityCargoPlane createPlane(Level world) {
        EntityCargoPlane plane = new EntityCargoPlane(world);
        plane.setPlaneType(type);
        return plane;
    }
}
