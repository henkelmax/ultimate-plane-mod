package de.maxhenkel.plane.item;

import de.maxhenkel.plane.Main;
import de.maxhenkel.plane.entity.EntityPlane;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemPlane extends Item {

    private EntityPlane.Type type;

    public ItemPlane(EntityPlane.Type type) {
        super(new Item.Properties().maxStackSize(1).group(ItemGroup.MISC));
        this.type = type;
        setRegistryName(new ResourceLocation(Main.MODID, "plane_" + type.getTypeName()));
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        if (!context.getFace().equals(Direction.UP)) {
            return ActionResultType.FAIL;
        }

        World world = context.getWorld();
        BlockPos pos = context.getPos();

        if (!world.getBlockState(pos.up()).isAir(world, pos)) {
            return ActionResultType.FAIL;
        }

        PlayerEntity player = context.getPlayer();

        EntityPlane plane = new EntityPlane(world);
        plane.setFuel(100);

        plane.setPositionAndRotation(pos.getX() + 0.5D, pos.getY() + 1.01D, pos.getZ() + 0.5D, context.getPlayer().rotationYaw, 0F);

        addData(context.getItem(), plane);

        plane.setPlaneType(type);

        world.addEntity(plane);

        if (!player.abilities.isCreativeMode) {
            context.getItem().setCount(context.getItem().getCount() - 1);
        }

        return ActionResultType.SUCCESS;
    }

    private void addData(ItemStack stack, EntityPlane plane) {
        CompoundNBT planeData = getPlaneData(stack);
        if (planeData != null) {
            plane.readAdditional(planeData);
            plane.setStarted(false, false);
        }

        if (!stack.getItem().getDisplayName(stack).equals(stack.getDisplayName())) {
            plane.setCustomName(stack.getDisplayName());
        }
    }

    private CompoundNBT getPlaneData(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if (tag == null) {
            return null;
        }

        if (!tag.contains("PlaneData")) {
            return null;
        }

        return tag.getCompound("PlaneData");
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        CompoundNBT planeData = getPlaneData(stack);

        if (planeData != null) {
            tooltip.add(
                    new TranslationTextComponent("plane.tooltip.plane.damage",
                            new StringTextComponent(String.valueOf(planeData.getFloat("Damage")))
                                    .applyTextStyle(TextFormatting.DARK_GRAY)
                    ).applyTextStyle(TextFormatting.GRAY));
            tooltip.add(
                    new TranslationTextComponent("plane.tooltip.plane.fuel",
                            new StringTextComponent(String.valueOf(planeData.getInt("Fuel")))
                                    .applyTextStyle(TextFormatting.DARK_GRAY)
                    ).applyTextStyle(TextFormatting.GRAY));
        }

        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
}
