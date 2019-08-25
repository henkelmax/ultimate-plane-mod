package de.maxhenkel.plane.events;

import de.maxhenkel.plane.entity.EntityPlane;
import de.maxhenkel.plane.item.ModItems;
import de.maxhenkel.plane.sound.ModSounds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class InteractEvents {

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent.EntityInteract evt) {
        PlayerEntity player = evt.getEntityPlayer();
        ItemStack stack = player.getHeldItem(Hand.MAIN_HAND);
        if (!stack.getItem().equals(ModItems.WRENCH)) {
            stack = player.getHeldItem(Hand.OFF_HAND);
            if (!stack.getItem().equals(ModItems.WRENCH)) {
                return;
            }
        }

        EntityPlane plane = null;

        if (!(evt.getTarget() instanceof EntityPlane)) {
            return;
        }

        if (evt.getTarget() instanceof EntityPlane) {
            plane = (EntityPlane) evt.getTarget();
        }

        if (plane == null) {
            return;
        }

        evt.setCanceled(true);

        float damage = plane.getPlaneDamage();

        if (damage <= 0F) {
            return;
        }

        stack.damageItem(1, player, playerEntity -> {
        });

        plane.setPlaneDamage(Math.max(damage - 1F, 0F));

        float newDamage = plane.getPlaneDamage();

        if (Math.round(newDamage) % 10 == 0) {
            ModSounds.playSound(ModSounds.RATCHET, evt.getWorld(), plane.getPosition(), null, SoundCategory.NEUTRAL, 1F);
        }
    }

}
