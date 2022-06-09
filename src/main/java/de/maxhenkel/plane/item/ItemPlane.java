package de.maxhenkel.plane.item;

import de.maxhenkel.plane.entity.EntityPlane;
import net.minecraft.world.level.Level;

public class ItemPlane extends ItemAbstractPlane<EntityPlane> {

    private EntityPlane.Type type;

    public ItemPlane(EntityPlane.Type type) {
        this.type = type;
    }

    @Override
    public EntityPlane createPlane(Level world) {
        EntityPlane plane = new EntityPlane(world);
        plane.setPlaneType(type);
        return plane;
    }
}
