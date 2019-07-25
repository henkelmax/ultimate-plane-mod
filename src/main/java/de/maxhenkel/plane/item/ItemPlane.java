package de.maxhenkel.plane.item;

import de.maxhenkel.plane.Main;
import de.maxhenkel.plane.entity.EntityPlane;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemPlane extends Item {

    public ItemPlane() {
        super(new Item.Properties().maxStackSize(1).group(ItemGroup.MISC));
        setRegistryName(new ResourceLocation(Main.MODID, "plane"));
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

        PlayerEntity player=context.getPlayer();

        EntityPlane plane = new EntityPlane(world);
        plane.setFuel(100);

        plane.setPositionAndRotation(pos.getX() + 0.5D, pos.getY() + 1.01D, pos.getZ() + 0.5D, context.getPlayer().rotationYaw, 0F);

        world.addEntity(plane);

        if(!player.abilities.isCreativeMode){
            context.getItem().setCount(context.getItem().getCount() - 1);
        }

        return ActionResultType.SUCCESS;
    }
}
