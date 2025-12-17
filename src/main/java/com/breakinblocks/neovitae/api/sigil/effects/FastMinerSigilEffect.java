package com.breakinblocks.neovitae.api.sigil.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import com.breakinblocks.neovitae.api.sigil.SigilEffect;
import com.breakinblocks.neovitae.common.damagesource.BMDamageSources;
import com.breakinblocks.neovitae.registry.SigilEffectRegistry;
import com.breakinblocks.neovitae.util.helper.PlayerHelper;

import java.util.List;
import java.util.function.Supplier;

/**
 * Fast Miner Sigil effect - grants Haste effect while active.
 * Also has an alchemy array effect that grants stronger Haste to nearby players.
 */
public record FastMinerSigilEffect(int amplifier) implements SigilEffect {
    public static final MapCodec<FastMinerSigilEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.optionalFieldOf("amplifier", 0).forGetter(FastMinerSigilEffect::amplifier)
    ).apply(instance, FastMinerSigilEffect::new));

    public static final Supplier<MapCodec<FastMinerSigilEffect>> REGISTRATION =
            SigilEffectRegistry.SIGIL_EFFECT_TYPES.register("fast_miner", () -> CODEC);

    @Override
    public MapCodec<? extends SigilEffect> codec() {
        return CODEC;
    }

    @Override
    public boolean isToggleable() {
        return true;
    }

    @Override
    public void activeTick(Level level, Player player, ItemStack stack, int itemSlot, boolean isSelected) {
        if (PlayerHelper.isFakePlayer(player)) {
            return;
        }
        // Grant Haste effect (2 ticks duration, so it wears off immediately when sigil is deactivated)
        player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 2, amplifier, true, false));
    }

    /**
     * Performs the alchemy array effect - grants Haste II to nearby players at the cost of 1 HP.
     */
    public boolean performArrayEffect(Level level, BlockPos pos) {
        double radius = 10;
        int ticks = 600;
        int potionPotency = 2;

        AABB bb = new AABB(pos).inflate(radius);
        List<Player> playerList = level.getEntitiesOfClass(Player.class, bb);

        for (Player player : playerList) {
            if (!player.hasEffect(MobEffects.DIG_SPEED) ||
                    (player.hasEffect(MobEffects.DIG_SPEED) &&
                            player.getEffect(MobEffects.DIG_SPEED).getAmplifier() < potionPotency)) {
                player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, ticks, potionPotency));
                if (!player.isCreative()) {
                    player.invulnerableTime = 0;
                    player.hurt(level.damageSources().source(BMDamageSources.SACRIFICE), 1.0F);
                }
            }
        }

        return false;
    }
}
