package com.breakinblocks.neovitae.common.event;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.tag.NVTags;

@EventBusSubscriber(modid = NeoVitae.MODID)
public class SacrificeSafetyHandler {
    public static final float MIN_REMAINING_HEALTH = 1.0F;

    private static boolean isSelfSacrifice(DamageSource source) {
        return source.is(NVTags.DamageTypes.SELF_SACRIFICE);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void capSacrificeDamage(LivingDamageEvent.Pre event) {
        if (!(event.getEntity() instanceof Player player) || !isSelfSacrifice(event.getSource())) {
            return;
        }
        float allowed = Math.max(0, player.getHealth() - MIN_REMAINING_HEALTH);
        if (event.getNewDamage() > allowed) {
            event.setNewDamage(allowed);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void preventSacrificeDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player) || !isSelfSacrifice(event.getSource())) {
            return;
        }
        event.setCanceled(true);
        player.setHealth(Math.min(player.getMaxHealth(), MIN_REMAINING_HEALTH));
    }
}
