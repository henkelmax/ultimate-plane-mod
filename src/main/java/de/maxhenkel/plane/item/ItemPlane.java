package de.maxhenkel.plane.item;

import de.maxhenkel.plane.PlaneType;
import de.maxhenkel.plane.entity.EntityPlane;
import net.minecraft.world.level.Level;

public class ItemPlane extends ItemAbstractPlane<EntityPlane> {

    public ItemPlane(PlaneType type, Properties properties) {
        super(type, properties);
    }

    @Override
    public EntityPlane createPlane(Level world) {
        EntityPlane plane = new EntityPlane(world);
        plane.setPlaneType(type);
        return plane;
    }
}
