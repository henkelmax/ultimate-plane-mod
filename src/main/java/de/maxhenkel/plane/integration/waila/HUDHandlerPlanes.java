package de.maxhenkel.plane.integration.waila;

import de.maxhenkel.corelib.math.MathUtils;
import de.maxhenkel.plane.entity.EntityPlane;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.EntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.config.IPluginConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

public class HUDHandlerPlanes implements IEntityComponentProvider {

    public static final ResourceLocation OBJECT_NAME_TAG = new ResourceLocation("waila", "object_name");

    public static final HUDHandlerPlanes INSTANCE = new HUDHandlerPlanes();

    @Override
    public void appendTooltip(ITooltip iTooltip, EntityAccessor entityAccessor, IPluginConfig iPluginConfig) {
        if (entityAccessor.getTooltipPosition().equals(TooltipPosition.HEAD)) {
            iTooltip.remove(OBJECT_NAME_TAG);
            iTooltip.add(new TextComponent(String.format(Waila.CONFIG.get().getFormatting().getBlockName(), entityAccessor.getEntity().getDisplayName().getString())).withStyle(ChatFormatting.WHITE));
        } else if (entityAccessor.getTooltipPosition().equals(TooltipPosition.BODY)) {
            if (entityAccessor.getEntity() instanceof EntityPlane plane) {
                iTooltip.add(new TranslatableComponent("tooltip.plane.fuel", new TextComponent(String.valueOf(plane.getFuel())).withStyle(ChatFormatting.DARK_GRAY)).withStyle(ChatFormatting.GRAY));
                iTooltip.add(new TranslatableComponent("tooltip.plane.damage", new TextComponent(String.valueOf(MathUtils.round(plane.getPlaneDamage(), 2))).withStyle(ChatFormatting.DARK_GRAY)).withStyle(ChatFormatting.GRAY));
            }
        }
    }

}