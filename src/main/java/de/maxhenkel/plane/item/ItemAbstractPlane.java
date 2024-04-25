package de.maxhenkel.plane.item;

import de.maxhenkel.corelib.math.MathUtils;
import de.maxhenkel.plane.entity.EntityPlaneSoundBase;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public abstract class ItemAbstractPlane<T extends EntityPlaneSoundBase> extends Item {

    public ItemAbstractPlane() {
        super(new Properties().stacksTo(1));
    }

    public abstract T createPlane(Level world);

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!context.getClickedFace().equals(Direction.UP)) {
            return InteractionResult.FAIL;
        }

        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();

        if (!world.getBlockState(pos.above()).getCollisionShape(world, pos).isEmpty()) {
            return InteractionResult.FAIL;
        }

        Player player = context.getPlayer();

        EntityPlaneSoundBase plane = createPlane(world);
        plane.setFuel(500);

        BlockState state = world.getBlockState(pos);
        VoxelShape collisionShape = state.getCollisionShape(world, pos);
        plane.absMoveTo(pos.getX() + 0.5D, pos.getY() + (collisionShape.isEmpty() ? 0D : state.getCollisionShape(world, pos).bounds().maxY) + 0.01D, pos.getZ() + 0.5D, context.getPlayer().getYRot(), 0F);

        addData(context.getItemInHand(), plane);

        world.addFreshEntity(plane);

        if (!player.getAbilities().instabuild) {
            context.getItemInHand().setCount(context.getItemInHand().getCount() - 1);
        }

        return InteractionResult.SUCCESS;
    }

    private void addData(ItemStack stack, EntityPlaneSoundBase plane) {
        PlaneData planeData = stack.get(ModItems.PLANE_DATA_COMPONENT);
        if (planeData != null) {
            plane.readAdditionalSaveData(planeData.getPlaneData());
            plane.setStarted(false, false);
        }
        Component customName = stack.get(DataComponents.CUSTOM_NAME);
        if (customName != null) {
            plane.setCustomName(customName);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag flagIn) {
        PlaneData planeData = stack.get(ModItems.PLANE_DATA_COMPONENT);
        if (planeData != null) {
            tooltip.add(
                    Component.translatable("tooltip.plane.damage",
                            Component.literal(String.valueOf(MathUtils.round(planeData.getDamage(), 2)))
                                    .withStyle(ChatFormatting.DARK_GRAY)
                    ).withStyle(ChatFormatting.GRAY));
            tooltip.add(
                    Component.translatable("tooltip.plane.fuel",
                            Component.literal(String.valueOf(planeData.getFuel()))
                                    .withStyle(ChatFormatting.DARK_GRAY)
                    ).withStyle(ChatFormatting.GRAY));
        }

        super.appendHoverText(stack, tooltipContext, tooltip, flagIn);
    }

}
