package de.maxhenkel.plane.net;

import net.minecraft.entity.EntitySize;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.IDataSerializer;

public class DataSerializerEntitySize {

    public static final IDataSerializer<EntitySize> ENTITY_SIZE = new IDataSerializer<EntitySize>() {

        @Override
        public void write(PacketBuffer packetBuffer, EntitySize size) {
            packetBuffer.writeFloat(size.width);
            packetBuffer.writeFloat(size.height);
            packetBuffer.writeBoolean(size.fixed);
        }

        public EntitySize read(PacketBuffer buf) {
            return new EntitySize(buf.readFloat(), buf.readFloat(), buf.readBoolean());
        }

        public DataParameter<EntitySize> createKey(int id) {
            return new DataParameter(id, this);
        }

        @Override
        public EntitySize copyValue(EntitySize size) {
            return new EntitySize(size.width, size.height, size.fixed);
        }
    };

}
