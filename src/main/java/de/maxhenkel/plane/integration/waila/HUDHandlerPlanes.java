package de.maxhenkel.plane.integration.waila;

import de.maxhenkel.corelib.math.MathUtils;
import de.maxhenkel.plane.Main;
import de.maxhenkel.plane.entity.EntityPlane;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class HUDHandlerPlanes implements IEntityComponentProvider {

    public static final ResourceLocation OBJECT_NAME_TAG = new ResourceLocation("jade", "object_name");

    public static final HUDHandlerPlanes INSTANCE = new HUDHandlerPlanes();

    public static final ResourceLocation UID = new ResourceLocation(Main.MODID, "plane");

    @Override
    public void appendTooltip(ITooltip iTooltip, EntityAccessor entityAccessor, IPluginConfig iPluginConfig) {
        if (entityAccessor.getEntity() instanceof EntityPlane plane) {
            iTooltip.remove(OBJECT_NAME_TAG);
            iTooltip.add(entityAccessor.getEntity().getDisplayName().copy().withStyle(ChatFormatting.WHITE));
            iTooltip.add(Component.translatable("tooltip.plane.fuel", Component.literal(String.valueOf(plane.getFuel())).withStyle(ChatFormatting.DARK_GRAY)).withStyle(ChatFormatting.GRAY));
            iTooltip.add(Component.translatable("tooltip.plane.damage", Component.literal(String.valueOf(MathUtils.round(plane.getPlaneDamage(), 2))).withStyle(ChatFormatting.DARK_GRAY)).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}