package de.maxhenkel.plane.item;

import de.maxhenkel.plane.Main;
import de.maxhenkel.plane.entity.EntityBushPlane;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ItemBushPlane extends ItemAbstractPlane<EntityBushPlane> {

    private EntityBushPlane.Type type;

    public ItemBushPlane(EntityBushPlane.Type type) {
        this.type = type;
        setRegistryName(new ResourceLocation(Main.MODID, "bush_plane_" + type.getTypeName()));
    }

    @Override
    public EntityBushPlane createPlane(World world) {
        EntityBushPlane plane = new EntityBushPlane(world);
        plane.setPlaneType(type);
        return plane;
    }
}
