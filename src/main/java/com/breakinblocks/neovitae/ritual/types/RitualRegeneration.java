package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.api.stream.StreamPresets;
import com.breakinblocks.neovitae.common.damagesource.NVDamageSources;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;
import com.breakinblocks.neovitae.api.will.SpiritusState;

import java.util.List;
import java.util.function.Consumer;

import net.minecraft.server.level.ServerLevel;
/**
 * Ritual that provides regeneration to nearby players.
 *
 * <p>Spiritus effects:
 * <ul>
 *   <li><b>Corrosive</b> - Vampire syphon: drains HP from mobs to heal players</li>
 * </ul>
 */
public class RitualRegeneration extends Ritual {

    public static final String HEAL_RANGE = "healRange";

    private static final double CORROSIVE_MIN_WILL = 10.0;
    private static final double CORROSIVE_WILL_PER_MOB = 0.5;

    public RitualRegeneration() {
        super("regeneration", 0, 500, "ritual." + NeoVitae.MODID + ".regeneration");
        addBlockRange(HEAL_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-5, -5, -5), 11, 11, 11));
        setMaximumVolumeAndDistanceOfRange(HEAL_RANGE, 2000, 15, 15);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) return;

        BlockPos masterPos = ctx.masterPos();

        // Normal regeneration for players
        List<Player> players = RitualHelper.getEntitiesInRange(ctx, this, HEAL_RANGE, Player.class);

        int cost = 0;
        for (Player player : players) {
            if (player.getHealth() < player.getMaxHealth()) {
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 0, true, false));
                cost += getRefreshCost();
                RitualHelper.chanceStream(ctx.level(), 20, () ->
                        StreamPresets.lifePulse(masterPos, player.blockPosition()).build()
                                .sendToNearby(ctx.serverLevel(), masterPos, 32));
            }
        }

        if (cost > 0) {
            ctx.syphon(Math.min(cost, ctx.currentEV()));
        }

        // Corrosive Will: Vampire syphon - drain HP from mobs to heal players
        SpiritusState will = RitualHelper.queryWill(ctx.level(), masterPos, CORROSIVE_MIN_WILL);
        if (will.hasCorrosive()) {
            List<LivingEntity> mobs = RitualHelper.getAliveMobsInRange(ctx, this, HEAL_RANGE);
            List<Player> hurtPlayers = players.stream()
                    .filter(p -> p.getHealth() < p.getMaxHealth())
                    .toList();

            if (!hurtPlayers.isEmpty()) {
                double willUsed = 0;
                int playerIndex = 0;

                for (LivingEntity mob : mobs) {
                    if ((will.getCorrosive() - willUsed) < CORROSIVE_WILL_PER_MOB) break;

                    float healthBefore = mob.getHealth();
                    mob.hurtServer((ServerLevel) mob.level(), ctx.level().damageSources().source(NVDamageSources.RITUAL), 2.0F);

                    if (mob.getHealth() < healthBefore) {
                        Player target = hurtPlayers.get(playerIndex % hurtPlayers.size());
                        target.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 40, 0, true, false));
                        playerIndex++;
                        willUsed += CORROSIVE_WILL_PER_MOB;
                        RitualHelper.chanceStream(ctx.level(), 8, () ->
                                StreamPresets.bloodTendril(mob, target.blockPosition()).build()
                                        .sendToNearby(ctx.serverLevel(), masterPos, 32));
                    }
                }

                if (willUsed > 0) {
                    will.use(SpiritusType.RUINA, willUsed);
                    will.drain(ctx.level(), masterPos);
                }
            }
        }
    }

    @Override
    public int getRefreshTime() {
        return 40;
    }

    @Override
    public int getRefreshCost() {
        return 50;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addCornerRunes(components, 1, 0, EnumRuneType.WATER);
        addParallelRunes(components, 2, 0, EnumRuneType.WATER);
        addCornerRunes(components, 2, 0, EnumRuneType.EARTH);
        addParallelRunes(components, 3, 0, EnumRuneType.EARTH);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualRegeneration();
    }
}
