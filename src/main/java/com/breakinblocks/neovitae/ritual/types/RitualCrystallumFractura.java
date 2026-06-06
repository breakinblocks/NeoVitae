package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.api.stream.StreamPresets;
import com.breakinblocks.neovitae.common.block.BlockSpiritusCrystal;
import com.breakinblocks.neovitae.common.blockentity.SpiritusCrystalBlockEntity;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.tag.NVTags;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;
import com.breakinblocks.neovitae.util.Utils;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;
import com.breakinblocks.neovitae.spiritus.SpiritusChunk;
import com.breakinblocks.neovitae.spiritus.WorldSpiritusHandler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class RitualCrystallumFractura extends Ritual {

    public static final String NAME = "crystallum_fractura";
    public static final String HARVEST_RANGE = "harvest";
    public static final String AURA_RANGE = "aura";
    public static final String CHEST_RANGE = "chest";

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
        addBlockRange(CHEST_RANGE, new AreaDescriptor.Rectangle(new BlockPos(0, 1, 0), 1, 1, 1));
        setMaximumVolumeAndDistanceOfRange(HARVEST_RANGE, 4000, 16, 16);
        setMaximumVolumeAndDistanceOfRange(AURA_RANGE, 4000, 16, 16);
        setMaximumVolumeAndDistanceOfRange(CHEST_RANGE, 1, 5, 5);
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

        RitualHelper.ChestOutput chest = RitualHelper.resolveChestOutput(ctx, this, CHEST_RANGE);
        BlockEntity chestTile = chest.tile();
        ResourceHandler<ItemResource> outputHandler = chestTile != null ? Utils.getInventory(chestTile, Direction.DOWN) : null;
        if (outputHandler != null && !chest.hasFreeSlot()) {
            return;
        }

        applyAuraBuffs(serverLevel, masterRitualStone, masterPos, bias, gameTime);

        FakePlayer fakePlayer = RitualHelper.createRitualFakePlayer(serverLevel, owner, "NeoVitae Crystallum Fractura");

        List<BlockPos> harvestPositions = RitualHelper.getRangePositions(ctx.master(), this, HARVEST_RANGE, masterPos);
        int totalCost = 0;
        boolean chestFull = false;

        for (BlockPos harvestPos : harvestPositions) {
            if (chestFull) break;
            if (totalCost + getRefreshCost() > ctx.currentEV()) break;

            BlockState state = ctx.level().getBlockState(harvestPos);
            if (!isFullyGrownHarvestable(state)) continue;
            if (!BlockProtectionHelper.canBreakBlock(ctx.level(), harvestPos, owner)) continue;

            int fortuneLevel = computeFortuneLevel(serverLevel, harvestPos);
            ItemStack tool = RitualHelper.createMiningTool(serverLevel, fortuneLevel >= 3, false);
            applyCustomFortune(tool, serverLevel, fortuneLevel);

            List<ItemStack> drops = RitualHelper.getBlockDrops(serverLevel, state, harvestPos, tool, fakePlayer);

            if (state.getBlock() instanceof BlockSpiritusCrystal) {
                int harvested = state.getValue(BlockSpiritusCrystal.AGE);
                if (harvested <= 0) continue;
                for (ItemStack drop : drops) {
                    drop.setCount(drop.getCount() * harvested);
                }
                resetCrystalAge(ctx.level(), harvestPos, state);
            } else {
                ctx.level().destroyBlock(harvestPos, false);
            }
            totalCost += getRefreshCost();

            if (fortuneLevel > 0) {
                consumeRawForFortune(serverLevel, harvestPos);
            }

            final BlockPos streamFrom = harvestPos.immutable();
            RitualHelper.chanceStream(ctx.level(), 6, () ->
                    StreamPresets.arcaneBolt(streamFrom, masterPos).build()
                            .sendToNearby(serverLevel, masterPos, 64));

            for (ItemStack drop : drops) {
                if (drop.isEmpty()) continue;
                if (outputHandler != null) {
                    ItemStack remainder = Utils.insertItemStacked(outputHandler, drop, false);
                    if (!remainder.isEmpty()) {
                        Block.popResource(ctx.level(), harvestPos, remainder);
                        chestFull = true;
                    }
                } else {
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
            if (state.hasProperty(BlockStateProperties.AGE_3)) {
                return state.getValue(BlockStateProperties.AGE_3) == 3;
            }
            if (state.hasProperty(BlockStateProperties.AGE_2)) {
                return state.getValue(BlockStateProperties.AGE_2) == 2;
            }
            return true;
        }
        return false;
    }

    private void resetCrystalAge(Level level, BlockPos pos, BlockState state) {
        level.setBlock(pos, state.setValue(BlockSpiritusCrystal.AGE, 0), Block.UPDATE_ALL);
        if (level.getBlockEntity(pos) instanceof SpiritusCrystalBlockEntity crystal) {
            crystal.progressToNextCrystal = 0;
            crystal.internalCounter = 0;
            crystal.setChanged();
        }
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
        Holder<Enchantment> ench = level.registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(Enchantments.FORTUNE);
        tool.enchant(ench, fortuneLevel);
    }

    private void consumeRawForFortune(ServerLevel level, BlockPos pos) {
        if (level.getRandom().nextInt(FORTUNE_CONSUMPTION_AVG_TICKS) < getRefreshTime()) {
            WorldSpiritusHandler.drainSpiritusFromChunk(level, pos, SpiritusType.RAW, 1.0);
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
