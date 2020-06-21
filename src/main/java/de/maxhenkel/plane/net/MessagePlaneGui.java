package de.maxhenkel.plane.net;

import de.maxhenkel.plane.entity.EntityPlaneSoundBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;

public class MessagePlaneGui implements Message<MessagePlaneGui> {

    private UUID uuid;
    private boolean outside;

    public MessagePlaneGui() {
        this.uuid = new UUID(0, 0);
    }

    public MessagePlaneGui(PlayerEntity player, boolean outside) {
        this.uuid = player.getUniqueID();
        this.outside = outside;
    }


    @Override
    public void executeServerSide(NetworkEvent.Context context) {
        if (!context.getSender().getUniqueID().equals(uuid)) {
            return;
        }

        Entity e = context.getSender().getRidingEntity();
        if (e instanceof EntityPlaneSoundBase) {
            ((EntityPlaneSoundBase) e).openGUI(context.getSender(), outside);
        }
    }

    @Override
    public void executeClientSide(NetworkEvent.Context context) {

    }

    @Override
    public MessagePlaneGui fromBytes(PacketBuffer buf) {
        this.uuid = buf.readUniqueId();
        this.outside = buf.readBoolean();
        return this;
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeUniqueId(uuid);
        buf.writeBoolean(outside);
    }
}
