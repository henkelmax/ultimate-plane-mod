package de.maxhenkel.plane.loottable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import de.maxhenkel.plane.Main;
import de.maxhenkel.plane.entity.EntityPlaneSoundBase;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.nbt.CompoundNBT;

public class CopyPlaneData extends LootFunction {

    protected CopyPlaneData(ILootCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        Entity entity = context.getParamOrNull(LootParameters.THIS_ENTITY);
        if (!(entity instanceof EntityPlaneSoundBase)) {
            return stack;
        }

        EntityPlaneSoundBase plane = (EntityPlaneSoundBase) entity;

        CompoundNBT planeData = new CompoundNBT();
        plane.addAdditionalSaveData(planeData);

        stack.getOrCreateTag().put("PlaneData", planeData);

        if (plane.hasCustomName()) {
            stack.setHoverName(plane.getCustomName());
        }

        return stack;
    }

    @Override
    public LootFunctionType getType() {
        return Main.COPY_PLANE_DATA;
    }

    public static class Serializer extends LootFunction.Serializer<CopyPlaneData> {

        @Override
        public CopyPlaneData deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, ILootCondition[] iLootConditions) {
            return new CopyPlaneData(iLootConditions);
        }
    }

}
