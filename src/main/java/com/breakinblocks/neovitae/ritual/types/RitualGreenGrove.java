package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.api.stream.StreamPresets;
import com.breakinblocks.neovitae.common.block.BlockSpiritusCrystal;
import com.breakinblocks.neovitae.common.blockentity.SpiritusCrystalBlockEntity;
import com.breakinblocks.neovitae.common.effect.NVMobEffects;
import com.breakinblocks.neovitae.common.tag.NVTags;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;
import com.breakinblocks.neovitae.api.spiritus.SpiritusState;

import java.util.List;
import java.util.function.Consumer;

/**
 * Ritual of the Green Grove - Accelerates plant growth in a configurable area.
 *
 * <p>Spiritus effects:
 * <ul>
 *   <li><b>Raw (Default)</b> - Reduces refresh time from 20 to 10 ticks</li>
 *   <li><b>Steadfast</b> - Hydrates farmland and plants seeds from ground items</li>
 *   <li><b>Corrosive</b> - Applies Plant Leech effect to nearby mobs (not players)</li>
 *   <li><b>Vengeful</b> - Scales growth success chance (0.3 + will/200)</li>
 * </ul>
 */
public class RitualGreenGrove extends Ritual {

    public static final String GROWTH_RANGE = "growthRange";
    public static final String LEECH_RANGE = "leechRange";
    public static final String HYDRATE_RANGE = "hydrateRange";

    private static final double MIN_DEFAULT = 10.0;
    private static final double MIN_STEADFAST = 10.0;
    private static final double MIN_CORROSIVE = 10.0;
    private static final double MIN_VENGEFUL = 10.0;

    private static final double WILL_PER_GROWTH = 0.2;
    private static final double WILL_PER_HYDRATE = 0.1;
    private static final double WILL_PER_LEECH = 1.0;
    private static final double WILL_PER_VENGEFUL_GROWTH = 0.3;

    private int refreshTime = 20;

    public RitualGreenGrove() {
        super("green_grove", 0, 1000, "ritual." + NeoVitae.MODID + ".green_grove");
        addBlockRange(GROWTH_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-4, 1, -4), 9, 9, 9));
        addBlockRange(LEECH_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-5, 0, -5), 11, 3, 11));
        addBlockRange(HYDRATE_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-3, 0, -3), 7, 1, 7));

        setMaximumVolumeAndDistanceOfRange(GROWTH_RANGE, 1000, 10, 10);
        setMaximumVolumeAndDistanceOfRange(LEECH_RANGE, 1500, 15, 10);
        setMaximumVolumeAndDistanceOfRange(HYDRATE_RANGE, 500, 10, 5);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) return;

        ServerLevel serverLevel = ctx.serverLevel();

        BlockPos masterPos = ctx.masterPos();

        SpiritusState will = RitualHelper.querySpiritus(ctx.level(), masterPos, MIN_DEFAULT);

        boolean doHydrate = will.hasSteadfast();
        boolean doLeech = will.hasCorrosive();
        boolean doVengeful = will.hasVengeful();

        refreshTime = scaleByRawSpiritus(will, 20, 10, 10);

        double steadfastSpiritusUsed = 0;
        double corrosiveSpiritusUsed = 0;
        double vengefulSpiritusUsed = 0;

        int maxGrowths = ctx.maxOperations(getRefreshCost());
        int totalGrowths = 0;

        // --- GROWTH: Bonemeal and random tick based growth ---
        List<BlockPos> positions = RitualHelper.getRangePositions(ctx.master(), this, GROWTH_RANGE, masterPos);
        for (BlockPos pos : positions) {
            if (totalGrowths >= maxGrowths) break;

            BlockState state = ctx.level().getBlockState(pos);
            Block block = state.getBlock();

            // Vengeful: scale growth chance
            if (doVengeful && (will.getVengeful() - vengefulSpiritusUsed) >= WILL_PER_VENGEFUL_GROWTH) {
                double growthChance = 0.3 + will.getVengeful() / 200.0;
                if (ctx.level().random.nextDouble() > growthChance) {
                    continue; // Skip this position based on chance
                }
            }

            boolean grew = false;

            // Try bonemeal-based growth first
            if (block instanceof BonemealableBlock growable) {
                if (growable.isValidBonemealTarget(ctx.level(), pos, state)) {
                    if (growable.isBonemealSuccess(ctx.level(), ctx.level().random, pos, state)) {
                        growable.performBonemeal(serverLevel, ctx.level().random, pos, state);
                        grew = true;
                    }
                }
            }

            // Support cactus, sugar cane, bamboo via randomTick
            if (!grew && (block == Blocks.CACTUS || block == Blocks.SUGAR_CANE || block == Blocks.BAMBOO)) {
                state.randomTick(serverLevel, pos, ctx.level().getRandom());
                grew = true;
            }

            if (!grew && state.is(NVTags.Blocks.GEODE_ACCELERATABLE)) {
                state.randomTick(serverLevel, pos, ctx.level().getRandom());
                grew = true;
            }

            if (!grew && block instanceof BlockSpiritusCrystal) {
                BlockEntity be = ctx.level().getBlockEntity(pos);
                if (be instanceof SpiritusCrystalBlockEntity crystal) {
                    double applied = crystal.growCrystalWithSpiritusAmount(0, 0.05);
                    if (applied > 0) grew = true;
                }
            }

            if (grew) {
                totalGrowths++;
                if (doVengeful && (will.getVengeful() - vengefulSpiritusUsed) >= WILL_PER_VENGEFUL_GROWTH) {
                    vengefulSpiritusUsed += WILL_PER_VENGEFUL_GROWTH;
                }
                RitualHelper.chanceStream(ctx.level(), 12, () ->
                        StreamPresets.lifePulse(masterPos, pos).color(0x44CC33).build()
                                .sendToNearby(ctx.serverLevel(), masterPos, 32));
            }
        }

        // --- STEADFAST: Hydrate farmland ---
        if (doHydrate) {
            List<BlockPos> hydratePositions = RitualHelper.getRangePositions(ctx.master(), this, HYDRATE_RANGE, masterPos);
            for (BlockPos pos : hydratePositions) {
                if ((will.getSteadfast() - steadfastSpiritusUsed) < WILL_PER_HYDRATE) break;

                BlockState state = ctx.level().getBlockState(pos);
                if (state.is(Blocks.FARMLAND)) {
                    int moisture = state.getValue(FarmBlock.MOISTURE);
                    if (moisture < 7) {
                        ctx.level().setBlock(pos, state.setValue(FarmBlock.MOISTURE, 7), Block.UPDATE_ALL);
                        steadfastSpiritusUsed += WILL_PER_HYDRATE;
                    }
                }
            }
        }

        // --- CORROSIVE: Apply Plant Leech to nearby mobs ---
        if (doLeech) {
            List<LivingEntity> mobs = RitualHelper.getAliveMobsInRange(ctx, this, LEECH_RANGE);

            for (LivingEntity mob : mobs) {
                if ((will.getCorrosive() - corrosiveSpiritusUsed) < WILL_PER_LEECH) break;

                if (!mob.hasEffect(NVMobEffects.PLANT_LEECH)) {
                    mob.addEffect(new MobEffectInstance(NVMobEffects.PLANT_LEECH, 200, 0));
                    corrosiveSpiritusUsed += WILL_PER_LEECH;
                }
            }
        }

        RitualHelper.drainSpiritus(will, ctx.level(), masterPos,
                0, corrosiveSpiritusUsed, 0, vengefulSpiritusUsed, steadfastSpiritusUsed);

        ctx.syphon(getRefreshCost() * totalGrowths);
    }

    @Override
    public int getRefreshTime() {
        return refreshTime;
    }

    @Override
    public int getRefreshCost() {
        return 20;
    }

    @Override
    public Component[] provideInformationOfRitualToPlayer(Player player) {
        return new Component[]{
                Component.translatable(getTranslationKey() + ".info"),
                Component.translatable(getTranslationKey() + ".will.default"),
                Component.translatable(getTranslationKey() + ".will.steadfast"),
                Component.translatable(getTranslationKey() + ".will.corrosive"),
                Component.translatable(getTranslationKey() + ".will.vengeful")
        };
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addCornerRunes(components, 1, 0, EnumRuneType.EARTH);
        addParallelRunes(components, 2, 0, EnumRuneType.EARTH);
        addCornerRunes(components, 2, 0, EnumRuneType.WATER);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualGreenGrove();
    }
}
