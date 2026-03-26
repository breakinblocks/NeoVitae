package com.breakinblocks.neovitae.common.event;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.NeoVitaeAPI;
import com.breakinblocks.neovitae.api.soul.ISoulNetwork;
import com.breakinblocks.neovitae.api.soul.SoulTicket;
import com.breakinblocks.neovitae.common.attribute.NVAttributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

/**
 * Handles Blood Siphon and Blood Shield attributes.
 *
 * Blood Siphon: converts a portion of damage dealt into LP.
 * Blood Shield: reduces incoming damage, draining LP to compensate.
 */
@EventBusSubscriber(modid = NeoVitae.MODID, bus = EventBusSubscriber.Bus.GAME)
public class BloodSiphonHandler {

    /**
     * Blood Siphon — on dealing damage, gain LP based on the attribute value.
     * Against players: drains from target's network, multiplied by config value (default 100).
     * Against non-players: generates LP, multiplied by config value (default 10).
     */
    @SubscribeEvent
    public static void onDamageDealt(LivingDamageEvent.Post event) {
        if (!(event.getSource().getEntity() instanceof Player attacker)) return;
        if (attacker.level().isClientSide) return;

        double siphonValue = attacker.getAttributeValue(NVAttributes.BLOOD_SIPHON);
        if (siphonValue <= 0) return;

        float damageDealt = event.getNewDamage();
        if (damageDealt <= 0) return;

        double lpBase = Math.min(siphonValue, damageDealt);
        boolean targetIsPlayer = event.getEntity() instanceof Player;
        int multiplier = targetIsPlayer
                ? NeoVitae.SERVER_CONFIG.BLOOD_SIPHON_PLAYER_MULTIPLIER.get()
                : NeoVitae.SERVER_CONFIG.BLOOD_SIPHON_MOB_MULTIPLIER.get();
        int lpAmount = (int) (lpBase * multiplier);
        if (lpAmount <= 0) return;

        ISoulNetwork attackerNetwork = NeoVitaeAPI.getInstance().getSoulNetwork(attacker.getUUID());
        if (attackerNetwork == null) return;

        if (targetIsPlayer) {
            Player targetPlayer = (Player) event.getEntity();
            ISoulNetwork targetNetwork = NeoVitaeAPI.getInstance().getSoulNetwork(targetPlayer.getUUID());
            if (targetNetwork != null) {
                targetNetwork.syphon(SoulTicket.create(lpAmount));
            }
        }

        attackerNetwork.add(SoulTicket.create(lpAmount), Integer.MAX_VALUE);
    }

    /**
     * Blood Shield — reduces incoming damage by 10% per point (capped at 99%).
     * Drains LP from the defender's soul network for the damage prevented.
     */
    @SubscribeEvent
    public static void onDamageTaken(LivingDamageEvent.Pre event) {
        if (!(event.getEntity() instanceof Player defender)) return;
        if (defender.level().isClientSide) return;

        double shieldValue = defender.getAttributeValue(NVAttributes.BLOOD_SHIELD);
        if (shieldValue <= 0) return;

        // 10% reduction per point, hard cap at 99%
        double reductionPercent = Math.min(shieldValue * 10.0, 99.0);
        float originalDamage = event.getOriginalDamage();
        float reducedDamage = originalDamage * (float) (1.0 - reductionPercent / 100.0);
        float damagePrevented = originalDamage - reducedDamage;

        if (damagePrevented <= 0) return;

        ISoulNetwork network = NeoVitaeAPI.getInstance().getSoulNetwork(defender.getUUID());
        if (network == null) return;

        int lpCost = (int) (damagePrevented * NeoVitae.SERVER_CONFIG.BLOOD_SHIELD_LP_COST_MULTIPLIER.get());

        // Only apply the shield if we can afford the LP cost
        int currentLP = network.getCurrentEssence();
        if (currentLP >= lpCost) {
            network.syphon(SoulTicket.create(lpCost));
            event.setNewDamage(reducedDamage);
        } else if (currentLP > 0) {
            // Partial shield — use whatever LP we have
            double affordableReduction = (double) currentLP / NeoVitae.SERVER_CONFIG.BLOOD_SHIELD_LP_COST_MULTIPLIER.get();
            float partialReduced = originalDamage - (float) affordableReduction;
            network.syphon(SoulTicket.create(currentLP));
            event.setNewDamage(Math.max(partialReduced, 0.1f));
        }
    }
}
