package de.maxhenkel.plane.net;

import de.maxhenkel.plane.entity.EntityPlane;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageControlPlane implements Message<MessageControlPlane> {

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
    public void executeServerSide(NetworkEvent.Context context) {
        Entity e = context.getSender().getRidingEntity();
        if (!(e instanceof EntityPlane)) {
            return;
        }

        EntityPlane plane = (EntityPlane) e;

        plane.updateControls(up, down, thrustPos, thrustNeg, left, right, braking, starting);
    }

    @Override
    public void executeClientSide(NetworkEvent.Context context) {

    }

    @Override
    public MessageControlPlane fromBytes(PacketBuffer buf) {
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
    public void toBytes(PacketBuffer buf) {
        buf.writeBoolean(up);
        buf.writeBoolean(down);
        buf.writeBoolean(thrustPos);
        buf.writeBoolean(thrustNeg);
        buf.writeBoolean(left);
        buf.writeBoolean(right);
        buf.writeBoolean(braking);
        buf.writeBoolean(starting);
    }
}
