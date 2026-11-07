package com.breakinblocks.neovitae.common.event;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.NeoVitaeAPI;
import com.breakinblocks.neovitae.api.soul.IAnima;
import com.breakinblocks.neovitae.api.soul.AnimaTicket;
import com.breakinblocks.neovitae.common.effect.AnimaSeveranceEffect;
import com.breakinblocks.neovitae.common.effect.NVMobEffects;
import com.breakinblocks.neovitae.common.network.SiphonStreamPayload;
import com.breakinblocks.neovitae.common.NVSounds;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.common.particle.NVParticles;
import com.breakinblocks.neovitae.api.stream.StreamEffect;
import com.breakinblocks.neovitae.common.attribute.NVAttributes;
import com.breakinblocks.neovitae.common.item.BloodOrbItem;
import com.breakinblocks.neovitae.common.item.OrbFluidHandler;
import com.breakinblocks.neovitae.common.item.sigil.ItemSigilDamned;
import com.breakinblocks.neovitae.common.fluid.NVFluids;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Handles Blood Siphon and Blood Shield attributes.
 *
 * Blood Siphon: converts a portion of damage dealt into LP.
 * Blood Shield: reduces incoming damage, draining LP to compensate.
 */
@EventBusSubscriber(modid = NeoVitae.MODID, bus = EventBusSubscriber.Bus.GAME)
public class BloodSiphonHandler {
    private static final int STREAM_INTERVAL = 10;
    private static final int STREAM_RANGE = 32;
    private static final Map<Player, Long> LAST_STREAM = new WeakHashMap<>();

    @SubscribeEvent
    public static void onDamageDealt(LivingDamageEvent.Post event) {
        if (!(event.getSource().getEntity() instanceof Player attacker)) return;
        if (attacker.level().isClientSide) return;

        double siphonValue = attacker.getAttributeValue(NVAttributes.BLOOD_SIPHON);
        if (siphonValue <= 0) return;

        float damageDealt = event.getNewDamage();
        if (damageDealt <= 0) return;

        double siphonPercent = Math.min(siphonValue, NeoVitae.SERVER_CONFIG.BLOOD_SIPHON_CAP.get());
        double lpBase = damageDealt * siphonPercent / 100.0;
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
                AnimaSeveranceEffect.withoutUpkeepPenalty(() -> targetNetwork.syphon(AnimaTicket.create(lpAmount)));
            }
            targetPlayer.addEffect(new MobEffectInstance(NVMobEffects.ANIMA_SEVERANCE, AnimaSeveranceEffect.DURATION_TICKS), attacker);
        }

        attackerNetwork.add(AnimaTicket.create(lpAmount), Integer.MAX_VALUE);
        if (attacker.level() instanceof ServerLevel serverLevel && streamReady(attacker, serverLevel.getGameTime())) {
            serverLevel.playSound(null, attacker.blockPosition(), NVSounds.BLOOD_SIPHON.get(), SoundSource.PLAYERS, 0.3f, 1.0f);
            StreamEffect.builder(event.getEntity()).toTracked(attacker)
                    .color(0x990011).scale(0.05f).speed(2.0f).gravity(0.25f)
                    .wobble(0.03f).wobbleFrequency(0.8f)
                    .spiralInto(true).spiralRadius(0.35f).spiralSpeed(0.35f)
                    .approachHeight(1.0f).alphaStart(0.08f).alphaEnd(0.95f)
                    .build().sendToNearby(serverLevel, attacker.blockPosition(), STREAM_RANGE,
                            effect -> new SiphonStreamPayload(false, effect));
        }
    }

    private static boolean streamReady(Player attacker, long time) {
        Long last = LAST_STREAM.get(attacker);
        if (last != null && time >= last && time - last < STREAM_INTERVAL) return false;
        LAST_STREAM.put(attacker, time);
        return true;
    }

    @SubscribeEvent
    public static void onDamageTaken(LivingDamageEvent.Pre event) {
        if (!(event.getEntity() instanceof Player defender)) return;
        if (defender.level().isClientSide) return;

        double shieldValue = defender.getAttributeValue(NVAttributes.BLOOD_SHIELD);
        if (shieldValue <= 0) return;

        double reductionPercent = Math.min(shieldValue, NeoVitae.SERVER_CONFIG.BLOOD_SHIELD_CAP.get());
        float currentDamage = event.getNewDamage();
        float reducedDamage = currentDamage * (float) (1.0 - reductionPercent / 100.0);
        float damagePrevented = currentDamage - reducedDamage;

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
            float partialReduced = currentDamage - (float) affordableReduction;
            network.syphon(AnimaTicket.create(currentLP));
            event.setNewDamage(Math.max(partialReduced, 0.1f));
            defender.level().playSound(null, defender.blockPosition(), NVSounds.BLOOD_SHIELD_ABSORB.get(), SoundSource.PLAYERS, 0.4f, 1.0f);
            if (defender.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_GLOW.get(), 0xAA0000), defender.getX(), defender.getY() + 1, defender.getZ(), 4, 0.3, 0.3, 0.3, 0);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide || player.tickCount % 10 != 0) return;
        ItemSigilDamned.updateBonuses(player);
    }

    @SubscribeEvent
    public static void onEntityKilled(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;

        ItemSigilDamned.updateBonuses(player);
        ItemSigilDamned.onKill(player, event.getEntity());

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
                    .build().sendToNearby(serverLevel, player.blockPosition(), 64,
                            effect -> new SiphonStreamPayload(true, effect));

            serverLevel.sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_GLOW.get(), 0xAA0000),
                    victim.getX(), victim.getY() + victim.getBbHeight() * 0.5, victim.getZ(),
                    6, 0.3, 0.3, 0.3, 0.02);
        }
    }
}
