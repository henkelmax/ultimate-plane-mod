package de.maxhenkel.plane;

import de.maxhenkel.plane.entity.EntityPlaneControlBase;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public class EntityPlane extends EntityPlaneControlBase {

    public EntityPlane(World world) {
        this(Main.PLANE_ENTITY_TYPE, world);
    }

    public EntityPlane(EntityType<?> type, World world) {
        super(type, world);
    }

}
