package de.maxhenkel.plane.net;

import de.maxhenkel.corelib.net.Message;
import de.maxhenkel.plane.entity.EntityPlaneSoundBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;

public class MessagePlaneGui implements Message<MessagePlaneGui> {

    private UUID uuid;
    private boolean outside;

    public MessagePlaneGui() {
        this.uuid = new UUID(0, 0);
    }

    public MessagePlaneGui(PlayerEntity player, boolean outside) {
        this.uuid = player.getUUID();
        this.outside = outside;
    }

    @Override
    public Dist getExecutingSide() {
        return Dist.DEDICATED_SERVER;
    }

    @Override
    public void executeServerSide(NetworkEvent.Context context) {
        if (!context.getSender().getUUID().equals(uuid)) {
            return;
        }

        Entity e = context.getSender().getVehicle();
        if (e instanceof EntityPlaneSoundBase) {
            ((EntityPlaneSoundBase) e).openGUI(context.getSender(), outside);
        }
    }

    @Override
    public MessagePlaneGui fromBytes(PacketBuffer buf) {
        this.uuid = buf.readUUID();
        this.outside = buf.readBoolean();
        return this;
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeUUID(uuid);
        buf.writeBoolean(outside);
    }

}
