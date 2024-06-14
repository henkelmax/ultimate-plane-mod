package de.maxhenkel.plane.net;

import de.maxhenkel.corelib.net.Message;
import de.maxhenkel.plane.Main;
import de.maxhenkel.plane.entity.EntityPlaneControlBase;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MessageControlPlane implements Message<MessageControlPlane> {

    public static final CustomPacketPayload.Type<MessageControlPlane> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Main.MODID, "control_plane"));

    private boolean up, down, thrustPos, thrustNeg, left, right, braking, starting;

    public MessageControlPlane() {
        this.up = false;
        this.down = false;
        this.thrustPos = false;
        this.thrustNeg = false;
        this.left = false;
        this.right = false;
        this.braking = false;
        this.starting = false;
    }

    public MessageControlPlane(boolean up, boolean down, boolean thrustPos, boolean thrustNeg, boolean left, boolean right, boolean braking, boolean starting) {
        this.up = up;
        this.down = down;
        this.thrustPos = thrustPos;
        this.thrustNeg = thrustNeg;
        this.left = left;
        this.right = right;
        this.braking = braking;
        this.starting = starting;
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

        if (!(sender.getVehicle() instanceof EntityPlaneControlBase plane)) {
            return;
        }

        plane.updateControls(up, down, thrustPos, thrustNeg, left, right, braking, starting);
    }

    @Override
    public MessageControlPlane fromBytes(RegistryFriendlyByteBuf buf) {
        up = buf.readBoolean();
        down = buf.readBoolean();
        thrustPos = buf.readBoolean();
        thrustNeg = buf.readBoolean();
        left = buf.readBoolean();
        right = buf.readBoolean();
        braking = buf.readBoolean();
        starting = buf.readBoolean();
        return this;
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeBoolean(up);
        buf.writeBoolean(down);
        buf.writeBoolean(thrustPos);
        buf.writeBoolean(thrustNeg);
        buf.writeBoolean(left);
        buf.writeBoolean(right);
        buf.writeBoolean(braking);
        buf.writeBoolean(starting);
    }

    @Override
    public Type<MessageControlPlane> type() {
        return TYPE;
    }

}
