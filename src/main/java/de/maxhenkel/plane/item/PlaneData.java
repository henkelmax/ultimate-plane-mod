package de.maxhenkel.plane.item;

import com.mojang.serialization.Codec;
import de.maxhenkel.plane.entity.EntityPlaneSoundBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Objects;

public class PlaneData {

    public static final Codec<PlaneData> CODEC = CompoundTag.CODEC.xmap(PlaneData::new, PlaneData::getPlaneData);

    public static final StreamCodec<RegistryFriendlyByteBuf, PlaneData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT,
            PlaneData::getDamage,
            ByteBufCodecs.INT,
            PlaneData::getFuel,
            ByteBufCodecs.COMPOUND_TAG,
            PlaneData::getPlaneData,
            PlaneData::new
    );

    private final float damage;
    private final int fuel;
    private final CompoundTag planeData;

    private PlaneData(float damage, int fuel, CompoundTag planeData) {
        this.damage = damage;
        this.fuel = fuel;
        this.planeData = planeData;
    }

    private PlaneData(CompoundTag planeData) {
        this.damage = planeData.getFloat("Damage");
        this.fuel = planeData.getInt("Fuel");
        this.planeData = planeData;
    }

    public static PlaneData of(EntityPlaneSoundBase entity) {
        CompoundTag saveData = new CompoundTag();
        entity.addAdditionalSaveData(saveData);
        return new PlaneData(saveData);
    }

    public static PlaneData of(CompoundTag tag) {
        return new PlaneData(tag.copy());
    }

    public float getDamage() {
        return damage;
    }

    public int getFuel() {
        return fuel;
    }

    public CompoundTag getPlaneData() {
        return planeData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PlaneData planeData1 = (PlaneData) o;
        return Float.compare(damage, planeData1.damage) == 0 && fuel == planeData1.fuel && Objects.equals(planeData, planeData1.planeData);
    }

    @Override
    public int hashCode() {
        int result = Float.hashCode(damage);
        result = 31 * result + fuel;
        result = 31 * result + Objects.hashCode(planeData);
        return result;
    }
}
