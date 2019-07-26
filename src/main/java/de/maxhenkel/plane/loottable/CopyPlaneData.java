package de.maxhenkel.plane.loottable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import de.maxhenkel.plane.Main;
import de.maxhenkel.plane.entity.EntityPlane;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

public class CopyPlaneData extends LootFunction {
    protected CopyPlaneData(ILootCondition[] conditions) {
        super(conditions);
    }

    @Override
    public ItemStack doApply(ItemStack stack, LootContext context) {
        Entity entity = context.get(LootParameters.THIS_ENTITY);
        if (!(entity instanceof EntityPlane)) {
            return stack;
        }

        EntityPlane plane = (EntityPlane) entity;

        CompoundNBT planeData = new CompoundNBT();
        plane.writeAdditional(planeData);

        stack.getOrCreateTag().put("PlaneData", planeData);

        if (plane.hasCustomName()) {
            stack.setDisplayName(plane.getCustomName());
        }

        return stack;
    }

    public static class Serializer extends net.minecraft.world.storage.loot.LootFunction.Serializer<CopyPlaneData> {

        public Serializer() {
            super(new ResourceLocation(Main.MODID, "copy_plane_data"), CopyPlaneData.class);
        }

        @Override
        public CopyPlaneData deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, ILootCondition[] iLootConditions) {
            return new CopyPlaneData(iLootConditions);
        }
    }
}
