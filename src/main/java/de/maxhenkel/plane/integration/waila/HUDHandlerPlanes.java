package de.maxhenkel.plane.integration.waila;

import de.maxhenkel.plane.MathTools;
import de.maxhenkel.plane.entity.EntityPlane;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITaggableList;
import mcp.mobius.waila.utils.ModIdentification;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;

import java.util.List;

public class HUDHandlerPlanes implements IEntityComponentProvider {

    static final ResourceLocation OBJECT_NAME_TAG = new ResourceLocation("waila", "object_name");
    static final ResourceLocation CONFIG_SHOW_REGISTRY = new ResourceLocation("waila", "show_registry");
    static final ResourceLocation REGISTRY_NAME_TAG = new ResourceLocation("waila", "registry_name");

    static final HUDHandlerPlanes INSTANCE = new HUDHandlerPlanes();

    @Override
    public void appendHead(List<ITextComponent> tip, IEntityAccessor accessor, IPluginConfig config) {
        ITaggableList<ResourceLocation, ITextComponent> tooltip = (ITaggableList<ResourceLocation, ITextComponent>) tip;
        tooltip.setTag(OBJECT_NAME_TAG, new StringTextComponent(String.format(Waila.CONFIG.get().getFormatting().getEntityName(), accessor.getEntity().getDisplayName().getFormattedText())));
        if (config.get(CONFIG_SHOW_REGISTRY)) {
            tooltip.setTag(REGISTRY_NAME_TAG, new StringTextComponent(String.format(Waila.CONFIG.get().getFormatting().getRegistryName(), accessor.getEntity().getDisplayName().getFormattedText())));
        }
    }

    @Override
    public void appendBody(List<ITextComponent> tooltip, IEntityAccessor accessor, IPluginConfig config) {
        if (!(accessor.getEntity() instanceof EntityPlane)) {
            return;
        }
        EntityPlane plane = (EntityPlane) accessor.getEntity();

        tooltip.add(new TranslationTextComponent("tooltip.plane.fuel", new StringTextComponent(String.valueOf(plane.getFuel())).applyTextStyle(TextFormatting.DARK_GRAY)).applyTextStyle(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent("tooltip.plane.damage", new StringTextComponent(String.valueOf(MathTools.round(plane.getPlaneDamage(), 2))).applyTextStyle(TextFormatting.DARK_GRAY)).applyTextStyle(TextFormatting.GRAY));
    }

    @Override
    public void appendTail(List<ITextComponent> tooltip, IEntityAccessor accessor, IPluginConfig config) {
        tooltip.add(new StringTextComponent(String.format(Waila.CONFIG.get().getFormatting().getModName(), ModIdentification.getModInfo(accessor.getEntity()).getName())));
    }
}