package de.maxhenkel.plane.entity;

import de.maxhenkel.plane.PlaneMod;
import de.maxhenkel.plane.gui.ContainerPlane;
import de.maxhenkel.plane.net.MessagePlaneGui;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import javax.annotation.Nullable;

public class EntityPlane extends EntityPlaneBase {

    private static final Vec3 BODY_CENTER = new Vec3(0D, 0D, -17.5D / 16D);

    public EntityPlane(Level world) {
        this(PlaneMod.PLANE_ENTITY_TYPE.get(), world);
    }

    public EntityPlane(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Override
    public float getPlayerScaleFactor() {
        return 0.8F;
    }

    @Override
    public void openGUI(Player player, boolean outside) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return getName();
                }

                @Nullable
                @Override
                public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
                    return new ContainerPlane(i, EntityPlane.this, playerInventory);
                }
            }, packetBuffer -> {
                packetBuffer.writeUUID(getUUID());
            });
        } else {
            ClientPacketDistributor.sendToServer(new MessagePlaneGui(player, outside));
        }
    }

    @Override
    public int getFuelCapacity() {
        return PlaneMod.SERVER_CONFIG.planeFuelCapacity.get();
    }

    @Override
    protected float getBaseFuelUsage() {
        return PlaneMod.SERVER_CONFIG.planeBaseFuelUsage.get().floatValue();
    }

    @Override
    public double getFallSpeed() {
        return 0.1D;
    }

    @Override
    public Vec3[] getPlayerOffsets() {
        return new Vec3[]{new Vec3(0D, 0D, 1D), new Vec3(0D, 0D, 0.5D), new Vec3(0D, 0D, 0D)};
    }

    @Override
    public ResourceKey<LootTable> getPlaneLootTable() {
        return ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(PlaneMod.MODID, "entities/plane_" + getPlaneType().getTypeName()));
    }

    @Override
    public Vec3 getBodyRotationCenter() {
        return BODY_CENTER;
    }
}
