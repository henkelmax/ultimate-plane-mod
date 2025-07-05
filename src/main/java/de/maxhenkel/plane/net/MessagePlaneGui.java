package de.maxhenkel.plane.net;

import de.maxhenkel.corelib.net.Message;
import de.maxhenkel.plane.PlaneMod;
import de.maxhenkel.plane.entity.EntityPlaneSoundBase;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public class MessagePlaneGui implements Message<MessagePlaneGui> {

    public static final CustomPacketPayload.Type<MessagePlaneGui> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(PlaneMod.MODID, "plane_gui"));

    private UUID uuid;
    private boolean outside;

    public MessagePlaneGui() {
        this.uuid = new UUID(0, 0);
    }

    public MessagePlaneGui(Player player, boolean outside) {
        this.uuid = player.getUUID();
        this.outside = outside;
    }

    @Override
    public PacketFlow getExecutingSide() {
        return PacketFlow.SERVERBOUND;
    }

    @Override
    public void executeServerSide(IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer sender)) {
            return;
        }

        if (!sender.getUUID().equals(uuid)) {
            return;
        }

        if (sender.getVehicle() instanceof EntityPlaneSoundBase plane) {
            plane.openGUI(sender, outside);
        }
    }

    @Override
    public MessagePlaneGui fromBytes(RegistryFriendlyByteBuf buf) {
        this.uuid = buf.readUUID();
        this.outside = buf.readBoolean();
        return this;
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeUUID(uuid);
        buf.writeBoolean(outside);
    }

    @Override
    public Type<MessagePlaneGui> type() {
        return TYPE;
    }

}
