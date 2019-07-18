package de.maxhenkel.plane.net;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.IDataSerializer;
import net.minecraft.util.math.Vec3d;

public class DataSerializerVec3d {

    public static final IDataSerializer<Vec3d> VEC3D = new IDataSerializer<Vec3d>() {

        @Override
        public void write(PacketBuffer packetBuffer, Vec3d vec) {
            packetBuffer.writeDouble(vec.x);
            packetBuffer.writeDouble(vec.y);
            packetBuffer.writeDouble(vec.z);
        }

        public Vec3d read(PacketBuffer buf) {
            return new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        }

        public DataParameter<Vec3d> createKey(int id) {
            return new DataParameter(id, this);
        }

        @Override
        public Vec3d copyValue(Vec3d vec) {
            return new Vec3d(vec.x, vec.y, vec.z);
        }
    };

}
