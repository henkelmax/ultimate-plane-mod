package de.maxhenkel.plane.net;

import de.maxhenkel.corelib.net.Message;
import de.maxhenkel.plane.Main;
import de.maxhenkel.plane.entity.EntityPlaneSoundBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.UUID;

public class MessagePlaneGui implements Message<MessagePlaneGui> {

    public static ResourceLocation ID = new ResourceLocation(Main.MODID, "plane_gui");

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
    public void executeServerSide(PlayPayloadContext context) {
        if (!(context.player().orElse(null) instanceof ServerPlayer sender)) {
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
    public MessagePlaneGui fromBytes(FriendlyByteBuf buf) {
        this.uuid = buf.readUUID();
        this.outside = buf.readBoolean();
        return this;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUUID(uuid);
        buf.writeBoolean(outside);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

}
