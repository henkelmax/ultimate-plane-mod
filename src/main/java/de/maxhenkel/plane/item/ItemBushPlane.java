package de.maxhenkel.plane.item;

import de.maxhenkel.plane.PlaneType;
import de.maxhenkel.plane.entity.EntityBushPlane;
import net.minecraft.world.level.Level;

public class ItemBushPlane extends ItemAbstractPlane<EntityBushPlane> {

    public ItemBushPlane(PlaneType type, Properties properties) {
        super(type, properties);
    }

    @Override
    public EntityBushPlane createPlane(Level world) {
        EntityBushPlane plane = new EntityBushPlane(world);
        plane.setPlaneType(type);
        return plane;
    }
}
