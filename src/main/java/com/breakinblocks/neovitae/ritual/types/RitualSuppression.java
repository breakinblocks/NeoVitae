// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2014-2026 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.ritual.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.api.stream.StreamPresets;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.block.SpectralBlock;
import com.breakinblocks.neovitae.common.blockentity.SpectralBlockEntity;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class RitualSuppression extends Ritual {

    public static final String SUPPRESS_RANGE = "suppressRange";
    private static final int HOLD_TICKS = 40;
    private static final int HOLD_JITTER = 10;
    private static final int MAX_STREAMS_PER_REFRESH = 2;

    private static final Codec<BlockStateEntry> ENTRY_CODEC = RecordCodecBuilder.create(b -> b.group(
            BlockPos.CODEC.fieldOf("pos").forGetter(BlockStateEntry::pos),
            BlockState.CODEC.fieldOf("state").forGetter(BlockStateEntry::state)
    ).apply(b, BlockStateEntry::new));

    private static final Codec<List<BlockStateEntry>> ENTRY_LIST_CODEC = ENTRY_CODEC.listOf();

    private record BlockStateEntry(BlockPos pos, BlockState state) {}

    private final Map<BlockPos, BlockState> suppressedBlocks = new HashMap<>();

    public RitualSuppression() {
        super("suppression", 0, 3000, "ritual." + NeoVitae.MODID + ".suppression");
        addBlockRange(SUPPRESS_RANGE, new AreaDescriptor.HemiSphere(BlockPos.ZERO, 10));
        setMaximumVolumeAndDistanceOfRange(SUPPRESS_RANGE, 2000, 15, 15);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) return;

        Level level = ctx.level();
        convertLegacySuppression(level);

        List<BlockPos> positions = RitualHelper.getRangePositions(ctx.master(), this, SUPPRESS_RANGE, ctx.masterPos());
        int fluidsSuppressed = 0;
        int maxFluids = ctx.maxOperations(getRefreshCost());
        int streams = 0;
        UUID owner = ctx.master().getOwner();

        for (BlockPos pos : positions) {
            if (level.getBlockEntity(pos) instanceof SpectralBlockEntity spectral) {
                hold(level, spectral);
                continue;
            }
            if (fluidsSuppressed >= maxFluids) continue;

            BlockState state = level.getBlockState(pos);
            if (!SpectralBlock.canSuppress(state)) continue;

            if (BlockProtectionHelper.tryReplaceBlock(level, pos, NVBlocks.SPECTRAL_BLOCK.get().defaultBlockState(), owner)
                    && level.getBlockEntity(pos) instanceof SpectralBlockEntity spectral) {
                spectral.setContainedBlockState(state);
                hold(level, spectral);
                fluidsSuppressed++;
                if (streams < MAX_STREAMS_PER_REFRESH && level.getRandom().nextInt(100) < 25) {
                    streams++;
                    StreamPresets.voidTendril(pos.immutable(), ctx.masterPos()).build()
                            .sendToNearby(ctx.serverLevel(), ctx.masterPos(), 64);
                }
            }
        }

        ctx.syphon(getRefreshCost() * fluidsSuppressed);
    }

    private static void hold(Level level, SpectralBlockEntity spectral) {
        spectral.resetDuration(HOLD_TICKS + level.getRandom().nextInt(HOLD_JITTER));
    }

    private void convertLegacySuppression(Level level) {
        if (suppressedBlocks.isEmpty()) return;
        for (Map.Entry<BlockPos, BlockState> entry : suppressedBlocks.entrySet()) {
            BlockPos pos = entry.getKey();
            if (!level.getBlockState(pos).isAir()) continue;
            level.setBlockAndUpdate(pos, NVBlocks.SPECTRAL_BLOCK.get().defaultBlockState());
            if (level.getBlockEntity(pos) instanceof SpectralBlockEntity spectral) {
                spectral.setContainedBlockState(entry.getValue());
                hold(level, spectral);
            }
        }
        suppressedBlocks.clear();
    }

    @Override
    public void stopRitual(IMasterRitualStone masterRitualStone, BreakType breakType) {
        Level level = masterRitualStone.getLevel();
        if (level == null || level.isClientSide()) return;

        for (Map.Entry<BlockPos, BlockState> entry : suppressedBlocks.entrySet()) {
            BlockPos pos = entry.getKey();
            BlockState state = entry.getValue();
            if (level.getBlockState(pos).isAir()) {
                level.setBlock(pos, state, Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_CLIENTS);
            }
        }
        suppressedBlocks.clear();
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addCornerRunes(components, 1, 0, EnumRuneType.WATER);
        addParallelRunes(components, 2, 0, EnumRuneType.WATER);
        addCornerRunes(components, 2, 0, EnumRuneType.AIR);
        addParallelRunes(components, 3, 0, EnumRuneType.EARTH);
        addCornerRunes(components, 3, 0, EnumRuneType.FIRE);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualSuppression();
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        List<BlockStateEntry> entries = suppressedBlocks.entrySet().stream()
                .map(e -> new BlockStateEntry(e.getKey(), e.getValue()))
                .toList();
        ENTRY_LIST_CODEC.encodeStart(NbtOps.INSTANCE, entries).result()
                .ifPresent(t -> tag.put("suppressedBlocks", t));
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        suppressedBlocks.clear();
        Tag raw = tag.get("suppressedBlocks");
        if (raw == null) return;
        ENTRY_LIST_CODEC.parse(NbtOps.INSTANCE, raw).result().ifPresent(list -> {
            for (BlockStateEntry entry : list) {
                suppressedBlocks.put(entry.pos(), entry.state());
            }
        });
    }
}
