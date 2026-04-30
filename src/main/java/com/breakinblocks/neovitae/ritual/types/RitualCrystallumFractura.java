package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.FakePlayer;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.api.stream.StreamPresets;
import com.breakinblocks.neovitae.common.block.BlockSpiritusCrystal;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.tag.NVTags;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;
import com.breakinblocks.neovitae.will.SpiritusChunk;
import com.breakinblocks.neovitae.will.WorldSpiritusHandler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class RitualCrystallumFractura extends Ritual {

    public static final String NAME = "crystallum_fractura";
    public static final String HARVEST_RANGE = "harvest";
    public static final String AURA_RANGE = "aura";

    private static final int FORTUNE_FLOOR_RAW = 30;
    private static final int FORTUNE_PEAK_RAW = 100;
    private static final int FORTUNE_CONSUMPTION_AVG_TICKS = 240;

    private static final double GROWTH_MULTIPLIER = 2.0;
    private static final double INJECTION_MULTIPLIER = 1.25;
    private static final long BUFF_DURATION_TICKS = 200L;

    public RitualCrystallumFractura() {
        super(NAME, 1, 100000, "ritual." + NeoVitae.MODID + "." + NAME);
        addBlockRange(HARVEST_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-7, -5, -7), 15, 11, 15));
        addBlockRange(AURA_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-7, -5, -7), 15, 11, 15));
        setMaximumVolumeAndDistanceOfRange(HARVEST_RANGE, 4000, 16, 16);
        setMaximumVolumeAndDistanceOfRange(AURA_RANGE, 4000, 16, 16);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) {
            masterRitualStone.stopRitual(BreakType.DEACTIVATE);
            return;
        }

        ServerLevel serverLevel = ctx.serverLevel();
        BlockPos masterPos = ctx.masterPos();
        UUID owner = ctx.master().getOwner();
        long gameTime = serverLevel.getGameTime();
        SpiritusType bias = masterRitualStone.getActiveSpiritusAspect();

        applyAuraBuffs(serverLevel, masterRitualStone, masterPos, bias, gameTime);

        FakePlayer fakePlayer = RitualHelper.createRitualFakePlayer(serverLevel, owner, "NeoVitae Crystallum Fractura");

        List<BlockPos> harvestPositions = RitualHelper.getRangePositions(ctx.master(), this, HARVEST_RANGE, masterPos);
        int totalCost = 0;

        for (BlockPos harvestPos : harvestPositions) {
            if (totalCost + getRefreshCost() > ctx.currentEV()) break;

            BlockState state = ctx.level().getBlockState(harvestPos);
            if (!isFullyGrownHarvestable(state)) continue;
            if (!BlockProtectionHelper.canBreakBlock(ctx.level(), harvestPos, owner)) continue;

            int fortuneLevel = computeFortuneLevel(serverLevel, harvestPos);
            ItemStack tool = RitualHelper.createMiningTool(serverLevel, fortuneLevel >= 3, false);
            applyCustomFortune(tool, serverLevel, fortuneLevel);

            List<ItemStack> drops = RitualHelper.getBlockDrops(serverLevel, state, harvestPos, tool, fakePlayer);

            ctx.level().destroyBlock(harvestPos, false);
            totalCost += getRefreshCost();

            if (fortuneLevel > 0) {
                consumeRawForFortune(serverLevel, harvestPos);
            }

            final BlockPos streamFrom = harvestPos.immutable();
            RitualHelper.chanceStream(ctx.level(), 6, () ->
                    StreamPresets.arcaneBolt(streamFrom, masterPos).build()
                            .sendToNearby(serverLevel, masterPos, 64));

            for (ItemStack drop : drops) {
                if (!drop.isEmpty()) {
                    Block.popResource(ctx.level(), harvestPos, drop);
                }
            }
        }

        ctx.syphon(totalCost > 0 ? totalCost : getRefreshCost());
    }

    private boolean isFullyGrownHarvestable(BlockState state) {
        if (state.getBlock() instanceof BlockSpiritusCrystal) {
            return state.hasProperty(BlockSpiritusCrystal.AGE)
                    && state.getValue(BlockSpiritusCrystal.AGE) == 6;
        }
        if (state.is(NVTags.Blocks.GEODE_HARVESTABLE)) {
            if (state.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.AGE_3)) {
                return state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.AGE_3) == 3;
            }
            if (state.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.AGE_2)) {
                return state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.AGE_2) == 2;
            }
            return true;
        }
        return false;
    }

    private int computeFortuneLevel(ServerLevel level, BlockPos pos) {
        SpiritusChunk chunk = WorldSpiritusHandler.getSpiritusChunk(level, pos);
        double raw = chunk.getSpiritus(SpiritusType.RAW);
        if (raw < FORTUNE_FLOOR_RAW) return 0;
        if (raw >= FORTUNE_PEAK_RAW) return 3;
        double frac = (raw - FORTUNE_FLOOR_RAW) / (double) (FORTUNE_PEAK_RAW - FORTUNE_FLOOR_RAW);
        return (int) Math.ceil(frac * 3);
    }

    private void applyCustomFortune(ItemStack tool, ServerLevel level, int fortuneLevel) {
        if (fortuneLevel <= 0) return;
        net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment> ench = level.registryAccess()
                .lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
                .getOrThrow(net.minecraft.world.item.enchantment.Enchantments.FORTUNE);
        tool.enchant(ench, fortuneLevel);
    }

    private void consumeRawForFortune(ServerLevel level, BlockPos pos) {
        if (level.getRandom().nextInt(FORTUNE_CONSUMPTION_AVG_TICKS) < getRefreshTime()) {
            WorldSpiritusHandler.drainWillFromChunk(level, pos, SpiritusType.RAW, 1.0);
        }
    }

    private void applyAuraBuffs(ServerLevel level, IMasterRitualStone master, BlockPos masterPos, SpiritusType bias, long gameTime) {
        AreaDescriptor range = RitualHelper.getEffectiveRange(master, this, AURA_RANGE);
        if (range == null) return;

        Set<Long> seen = new HashSet<>();
        for (BlockPos pos : range.getContainedPositions(masterPos)) {
            long key = (((long) (pos.getX() >> 4)) << 32) | ((pos.getZ() >> 4) & 0xFFFFFFFFL);
            if (!seen.add(key)) continue;

            SpiritusChunk chunk = WorldSpiritusHandler.getSpiritusChunk(level, pos);
            chunk.setGrowthMultiplier(GROWTH_MULTIPLIER, BUFF_DURATION_TICKS, gameTime);
            chunk.setInjectionMultiplier(INJECTION_MULTIPLIER, bias, BUFF_DURATION_TICKS, gameTime);
        }
    }

    @Override
    public int getRefreshTime() {
        return 100;
    }

    @Override
    public int getRefreshCost() {
        return 200;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addCornerRunes(components, 1, 0, EnumRuneType.EARTH);
        addParallelRunes(components, 1, 0, EnumRuneType.AIR);
        addCornerRunes(components, 2, 0, EnumRuneType.DUSK);
        addParallelRunes(components, 2, 0, EnumRuneType.FIRE);
        addCornerRunes(components, 3, 0, EnumRuneType.DUSK);
        addParallelRunes(components, 3, 0, EnumRuneType.EARTH);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualCrystallumFractura();
    }
}
