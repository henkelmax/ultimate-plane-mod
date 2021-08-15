package de.maxhenkel.plane.loottable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import de.maxhenkel.plane.Main;
import de.maxhenkel.plane.entity.EntityPlaneSoundBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class CopyPlaneData extends LootItemConditionalFunction {

    protected CopyPlaneData(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        if (!(entity instanceof EntityPlaneSoundBase)) {
            return stack;
        }

        EntityPlaneSoundBase plane = (EntityPlaneSoundBase) entity;

        CompoundTag planeData = new CompoundTag();
        plane.addAdditionalSaveData(planeData);

        stack.getOrCreateTag().put("PlaneData", planeData);

        if (plane.hasCustomName()) {
            stack.setHoverName(plane.getCustomName());
        }

        return stack;
    }

    @Override
    public LootItemFunctionType getType() {
        return Main.COPY_PLANE_DATA;
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<CopyPlaneData> {

        @Override
        public CopyPlaneData deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] iLootConditions) {
            return new CopyPlaneData(iLootConditions);
        }
    }

}
