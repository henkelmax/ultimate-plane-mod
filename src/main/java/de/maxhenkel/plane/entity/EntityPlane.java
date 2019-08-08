package de.maxhenkel.plane.entity;

import de.maxhenkel.plane.Main;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

public class EntityPlane extends EntityPlaneSoundBase {

    public static final float SCALE_FACTOR = 0.8F;

    private static final DataParameter<Integer> TYPE = EntityDataManager.createKey(EntityPlane.class, DataSerializers.VARINT);

    public EntityPlane(World world) {
        this(Main.PLANE_ENTITY_TYPE, world);
    }

    public EntityPlane(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putString("Type", getPlaneType().getTypeName());
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        setPlaneType(Type.fromTypeName(compound.getString("Type")));
    }

    @Override
    protected void registerData() {
        super.registerData();
        dataManager.register(TYPE, 0);
    }

    public Type getPlaneType() {
        return Type.values()[dataManager.get(TYPE)];
    }

    public void setPlaneType(Type type) {
        dataManager.set(TYPE, type.ordinal());
    }

    public static enum Type {
        OAK("oak"),
        SPRUCE("spruce"),
        BIRCH("birch"),
        JUNGLE("jungle"),
        ACACIA("acacia"),
        DARK_OAK("dark_oak");

        private String name;

        Type(String name) {
            this.name = name;
        }

        public String getTypeName() {
            return name;
        }

        public static Type fromTypeName(String name) {
            for (Type type : values()) {
                if (type.getTypeName().equals(name)) {
                    return type;
                }
            }
            return OAK;
        }
    }
}
