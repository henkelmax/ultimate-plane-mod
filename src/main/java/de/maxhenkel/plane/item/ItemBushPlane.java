package de.maxhenkel.plane.item;

import de.maxhenkel.plane.entity.EntityBushPlane;
import net.minecraft.world.level.Level;

public class ItemBushPlane extends ItemAbstractPlane<EntityBushPlane> {

    private EntityBushPlane.Type type;

    public ItemBushPlane(EntityBushPlane.Type type) {
        this.type = type;
    }

    @Override
    public EntityBushPlane createPlane(Level world) {
        EntityBushPlane plane = new EntityBushPlane(world);
        plane.setPlaneType(type);
        return plane;
    }
}
