package de.maxhenkel.plane.item;

import de.maxhenkel.plane.PlaneType;
import de.maxhenkel.plane.entity.EntityTransporterPlane;
import net.minecraft.world.level.Level;

public class ItemTransporterPlane extends ItemAbstractPlane<EntityTransporterPlane> {

    public ItemTransporterPlane(PlaneType type) {
        super(type);
    }

    @Override
    public EntityTransporterPlane createPlane(Level world) {
        EntityTransporterPlane plane = new EntityTransporterPlane(world);
        plane.setPlaneType(type);
        return plane;
    }
}
