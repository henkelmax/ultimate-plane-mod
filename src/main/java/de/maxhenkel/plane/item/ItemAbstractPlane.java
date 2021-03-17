package de.maxhenkel.plane.item;

import de.maxhenkel.corelib.math.MathUtils;
import de.maxhenkel.plane.entity.EntityPlaneSoundBase;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public abstract class ItemAbstractPlane<T extends EntityPlaneSoundBase> extends Item {

    public ItemAbstractPlane() {
        super(new Properties().stacksTo(1).tab(ItemGroup.TAB_TRANSPORTATION));
    }

    public abstract T createPlane(World world);

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        if (!context.getClickedFace().equals(Direction.UP)) {
            return ActionResultType.FAIL;
        }

        World world = context.getLevel();
        BlockPos pos = context.getClickedPos();

        if (!world.getBlockState(pos.above()).getCollisionShape(world, pos).isEmpty()) {
            return ActionResultType.FAIL;
        }

        PlayerEntity player = context.getPlayer();

        EntityPlaneSoundBase plane = createPlane(world);
        plane.setFuel(100);

        BlockState state = world.getBlockState(pos);
        VoxelShape collisionShape = state.getCollisionShape(world, pos);
        plane.absMoveTo(pos.getX() + 0.5D, pos.getY() + (collisionShape.isEmpty() ? 0D : state.getCollisionShape(world, pos).bounds().maxY) + 0.01D, pos.getZ() + 0.5D, context.getPlayer().yRot, 0F);

        addData(context.getItemInHand(), plane);

        world.addFreshEntity(plane);

        if (!player.abilities.instabuild) {
            context.getItemInHand().setCount(context.getItemInHand().getCount() - 1);
        }

        return ActionResultType.SUCCESS;
    }

    private void addData(ItemStack stack, EntityPlaneSoundBase plane) {
        CompoundNBT planeData = getPlaneData(stack);
        if (planeData != null) {
            plane.readAdditionalSaveData(planeData);
            plane.setStarted(false, false);
        }

        if (!stack.getItem().getName(stack).equals(stack.getDisplayName())) {
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
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        CompoundNBT planeData = getPlaneData(stack);

        if (planeData != null) {
            tooltip.add(
                    new TranslationTextComponent("tooltip.plane.damage",
                            new StringTextComponent(String.valueOf(MathUtils.round(planeData.getFloat("Damage"), 2)))
                                    .withStyle(TextFormatting.DARK_GRAY)
                    ).withStyle(TextFormatting.GRAY));
            tooltip.add(
                    new TranslationTextComponent("tooltip.plane.fuel",
                            new StringTextComponent(String.valueOf(planeData.getInt("Fuel")))
                                    .withStyle(TextFormatting.DARK_GRAY)
                    ).withStyle(TextFormatting.GRAY));
        }

        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }

}
