package de.maxhenkel.plane.loottable;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.maxhenkel.plane.Main;
import de.maxhenkel.plane.entity.EntityPlaneSoundBase;
import de.maxhenkel.plane.item.ModItems;
import de.maxhenkel.plane.item.PlaneData;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;

public class CopyPlaneData extends LootItemConditionalFunction {

    public static final MapCodec<CopyPlaneData> CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance).apply(instance, CopyPlaneData::new));

    protected CopyPlaneData(List<LootItemCondition> conditions) {
        super(conditions);
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        Entity entity = context.getOptionalParameter(LootContextParams.THIS_ENTITY);
        if (!(entity instanceof EntityPlaneSoundBase plane)) {
            return stack;
        }

        stack.set(ModItems.PLANE_DATA_COMPONENT, PlaneData.of(plane));

        if (plane.hasCustomName()) {
            stack.set(DataComponents.CUSTOM_NAME, plane.getCustomName());
        }

        return stack;
    }

    @Override
    public LootItemFunctionType getType() {
        return Main.COPY_PLANE_DATA.get();
    }

}
