package de.maxhenkel.plane.item;

import de.maxhenkel.plane.entity.EntityCargoPlane;
import net.minecraft.world.level.Level;

public class ItemCargoPlane extends ItemAbstractPlane<EntityCargoPlane> {

    private EntityCargoPlane.Type type;

    public ItemCargoPlane(EntityCargoPlane.Type type) {
        this.type = type;
    }

    @Override
    public EntityCargoPlane createPlane(Level world) {
        EntityCargoPlane plane = new EntityCargoPlane(world);
        plane.setPlaneType(type);
        return plane;
    }
}
