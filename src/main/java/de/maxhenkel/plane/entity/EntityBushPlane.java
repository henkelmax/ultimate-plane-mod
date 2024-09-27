package de.maxhenkel.plane.entity;

import de.maxhenkel.plane.Main;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class EntityBushPlane extends EntityPlaneBase {

    public EntityBushPlane(Level world) {
        this(Main.BUSH_PLANE_ENTITY_TYPE.get(), world);
        lootTable = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(Main.MODID, "entities/bush_plane_" + getPlaneType().getTypeName()));
    }

    public EntityBushPlane(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Override
    public float getPlayerScaleFactor() {
        return 0.8F;
    }

    @Override
    public void openGUI(Player player, boolean outside) {

    }

    @Override
    public int getFuelCapacity() {
        return 6000;
    }

    @Override
    protected float getBaseFuelUsage() {
        return 0.75F;
    }

    @Override
    public double getFallSpeed() {
        return 0.08D;
    }

    @Override
    public Vec3[] getPlayerOffsets() {
        return new Vec3[]{new Vec3(0D, 0D, 0.5D)};
    }

}
