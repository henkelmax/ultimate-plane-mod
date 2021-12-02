package de.maxhenkel.plane.net;

import de.maxhenkel.corelib.net.Message;
import de.maxhenkel.plane.entity.EntityPlaneControlBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.network.NetworkEvent;

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
    public Dist getExecutingSide() {
        return Dist.DEDICATED_SERVER;
    }

    @Override
    public void executeServerSide(NetworkEvent.Context context) {
        Entity e = context.getSender().getVehicle();
        if (!(e instanceof EntityPlaneControlBase)) {
            return;
        }

        EntityPlaneControlBase plane = (EntityPlaneControlBase) e;

        plane.updateControls(up, down, thrustPos, thrustNeg, left, right, braking, starting);
    }

    @Override
    public MessageControlPlane fromBytes(FriendlyByteBuf buf) {
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
    public void toBytes(FriendlyByteBuf buf) {
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
