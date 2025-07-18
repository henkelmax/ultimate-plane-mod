package de.maxhenkel.plane.entity;

import de.maxhenkel.plane.PlaneMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.Vec3;

public class EntityBushPlane extends EntityPlaneBase {

    private static final Vec3 BODY_CENTER = new Vec3(0D, 0D, -17.5D / 16D);

    public EntityBushPlane(Level world) {
        this(PlaneMod.BUSH_PLANE_ENTITY_TYPE.get(), world);
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
        return PlaneMod.SERVER_CONFIG.bushPlaneFuelCapacity.get();
    }

    @Override
    protected float getBaseFuelUsage() {
        return PlaneMod.SERVER_CONFIG.bushPlaneBaseFuelUsage.get().floatValue();
    }

    @Override
    public double getFallSpeed() {
        return 0.08D;
    }

    @Override
    public Vec3[] getPlayerOffsets() {
        return new Vec3[]{new Vec3(0D, 0D, 0.5D)};
    }

    @Override
    public ResourceKey<LootTable> getPlaneLootTable() {
        return ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(PlaneMod.MODID, "entities/bush_plane_" + getPlaneType().getTypeName()));
    }

    @Override
    public Vec3 getBodyRotationCenter() {
        return BODY_CENTER;
    }
}
