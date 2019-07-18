package de.maxhenkel.plane.entity;

import de.maxhenkel.plane.Main;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public class EntityPlane extends EntityPlaneSoundBase {

    public static final float SCALE_FACTOR = 1.0F;

    public EntityPlane(World world) {
        this(Main.PLANE_ENTITY_TYPE, world);
    }

    public EntityPlane(EntityType<?> type, World world) {
        super(type, world);
    }

}
