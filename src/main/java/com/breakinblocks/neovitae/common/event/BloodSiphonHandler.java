package com.breakinblocks.neovitae.common.event;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.NeoVitaeAPI;
import com.breakinblocks.neovitae.api.soul.IAnima;
import com.breakinblocks.neovitae.api.soul.AnimaTicket;
import com.breakinblocks.neovitae.common.NVSounds;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.common.particle.NVParticles;
import com.breakinblocks.neovitae.api.stream.StreamEffect;
import com.breakinblocks.neovitae.common.attribute.NVAttributes;
import com.breakinblocks.neovitae.common.item.BloodOrbItem;
import com.breakinblocks.neovitae.common.item.OrbFluidHandler;
import com.breakinblocks.neovitae.common.fluid.NVFluids;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;

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

        IAnima attackerNetwork = NeoVitaeAPI.getInstance().getAnima(attacker.getUUID());
        if (attackerNetwork == null) return;

        if (targetIsPlayer) {
            Player targetPlayer = (Player) event.getEntity();
            IAnima targetNetwork = NeoVitaeAPI.getInstance().getAnima(targetPlayer.getUUID());
            if (targetNetwork != null) {
                targetNetwork.syphon(AnimaTicket.create(lpAmount));
            }
        }

        attackerNetwork.add(AnimaTicket.create(lpAmount), Integer.MAX_VALUE);
        attacker.level().playSound(null, attacker.blockPosition(), NVSounds.BLOOD_SIPHON.get(), SoundSource.PLAYERS, 0.3f, 1.0f);
        if (attacker.level() instanceof ServerLevel serverLevel) {
            StreamEffect.builder(event.getEntity()).toTracked(attacker)
                    .color(0x990011).scale(0.05f).speed(2.0f).gravity(0.25f)
                    .wobble(0.03f).wobbleFrequency(0.8f)
                    .spiralInto(true).spiralRadius(0.35f).spiralSpeed(0.35f)
                    .approachHeight(1.0f).alphaStart(0.08f).alphaEnd(0.95f)
                    .build().sendToNearby(serverLevel, attacker.blockPosition(), 128);
        }
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

        IAnima network = NeoVitaeAPI.getInstance().getAnima(defender.getUUID());
        if (network == null) return;

        int lpCost = (int) (damagePrevented * NeoVitae.SERVER_CONFIG.BLOOD_SHIELD_LP_COST_MULTIPLIER.get());

        // Only apply the shield if we can afford the EV cost
        int currentLP = network.getCurrentEV();
        if (currentLP >= lpCost) {
            network.syphon(AnimaTicket.create(lpCost));
            event.setNewDamage(reducedDamage);
            defender.level().playSound(null, defender.blockPosition(), NVSounds.BLOOD_SHIELD_ABSORB.get(), SoundSource.PLAYERS, 0.4f, 1.0f);
            if (defender.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_GLOW.get(), 0xAA0000), defender.getX(), defender.getY() + 1, defender.getZ(), 4, 0.3, 0.3, 0.3, 0);
            }
        } else if (currentLP > 0) {
            // Partial shield — use whatever LP we have
            double affordableReduction = (double) currentLP / NeoVitae.SERVER_CONFIG.BLOOD_SHIELD_LP_COST_MULTIPLIER.get();
            float partialReduced = originalDamage - (float) affordableReduction;
            network.syphon(AnimaTicket.create(currentLP));
            event.setNewDamage(Math.max(partialReduced, 0.1f));
            defender.level().playSound(null, defender.blockPosition(), NVSounds.BLOOD_SHIELD_ABSORB.get(), SoundSource.PLAYERS, 0.4f, 1.0f);
            if (defender.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_GLOW.get(), 0xAA0000), defender.getX(), defender.getY() + 1, defender.getZ(), 4, 0.3, 0.3, 0.3, 0);
            }
        }
    }

    @SubscribeEvent
    public static void onEntityKilled(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;

        ItemStack offhand = player.getOffhandItem();
        if (offhand.isEmpty() || !(offhand.getItem() instanceof BloodOrbItem)) return;

        LivingEntity victim = event.getEntity();
        float maxHealth = victim.getMaxHealth();
        int baseEV = (int) (maxHealth * 10);

        double sacrificeBonus = player.getAttributeValue(NVAttributes.BONUS_SACRIFICE);
        int totalEV = (int) (baseEV * (1.0 + sacrificeBonus / 100.0));

        if (totalEV <= 0) return;

        FluidStack blood = new FluidStack(NVFluids.ESSENTIA_VITAE_SOURCE.get(), totalEV);
        int filled = OrbFluidHandler.fillInternal(offhand, blood, FluidAction.EXECUTE);

        if (filled > 0) {
            ServerLevel serverLevel = player.serverLevel();

            serverLevel.playSound(null, player.blockPosition(), NVSounds.BLOOD_SIPHON.get(), SoundSource.PLAYERS, 0.4f, 0.8f);

            StreamEffect.builder(victim)
                    .toTracked(player)
                    .color(0x990011).scale(0.06f).speed(2.5f).gravity(0.15f)
                    .wobble(0.02f).wobbleFrequency(0.8f)
                    .spiralInto(true).spiralRadius(0.25f).spiralSpeed(0.3f)
                    .approachHeight(0.8f).alphaStart(0.1f).alphaEnd(0.9f)
                    .build().sendToNearby(serverLevel, player.blockPosition(), 64);

            serverLevel.sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_GLOW.get(), 0xAA0000),
                    victim.getX(), victim.getY() + victim.getBbHeight() * 0.5, victim.getZ(),
                    6, 0.3, 0.3, 0.3, 0.02);
        }
    }
}
