package de.maxhenkel.plane.events;

import de.maxhenkel.plane.entity.EntityPlaneBase;
import de.maxhenkel.plane.sound.ModSounds;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import de.maxhenkel.plane.item.ModItems;

public class InteractEvents {

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent.EntityInteract evt) {
        Player player = evt.getEntity();
        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (!stack.is(ModItems.WRENCH)) {
            stack = player.getItemInHand(InteractionHand.OFF_HAND);
            if (!stack.is(ModItems.WRENCH)) {
                return;
            }
        }

        if (!(evt.getTarget() instanceof EntityPlaneBase plane)) {
            return;
        }

        evt.setCanceled(true);

        float damage = plane.getPlaneDamage();

        if (damage <= 0F) {
            return;
        }

        if (player instanceof ServerPlayer serverPlayer) {
            stack.hurtAndBreak(1, serverPlayer.serverLevel(), serverPlayer, (item) -> {
            });
        }

        plane.setPlaneDamage(Math.max(damage - 1F, 0F));

        float newDamage = plane.getPlaneDamage();

        if (Math.round(newDamage) % 10 == 0) {
            ModSounds.playSound(ModSounds.RATCHET.get(), evt.getLevel(), plane.blockPosition(), null, SoundSource.NEUTRAL, 1F);
        }
    }

}
