package com.breakinblocks.neovitae.ritual.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.api.stream.StreamPresets;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Ritual that suppresses (temporarily removes) fluids in an area.
 * Fluids are replaced with air while the ritual is active.
 */
public class RitualSuppression extends Ritual {

    public static final String SUPPRESS_RANGE = "suppressRange";

    private static final Codec<BlockStateEntry> ENTRY_CODEC = RecordCodecBuilder.create(b -> b.group(
            BlockPos.CODEC.fieldOf("pos").forGetter(BlockStateEntry::pos),
            BlockState.CODEC.fieldOf("state").forGetter(BlockStateEntry::state)
    ).apply(b, BlockStateEntry::new));

    private static final Codec<List<BlockStateEntry>> ENTRY_LIST_CODEC = ENTRY_CODEC.listOf();

    private record BlockStateEntry(BlockPos pos, BlockState state) {}

    // Track suppressed blocks to restore them when ritual stops
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

        List<BlockPos> positions = RitualHelper.getRangePositions(ctx.master(), this, SUPPRESS_RANGE, ctx.masterPos());
        int fluidsSuppressed = 0;
        int maxFluids = ctx.maxOperations(getRefreshCost());
        UUID owner = ctx.master().getOwner();

        for (BlockPos pos : positions) {
            if (fluidsSuppressed >= maxFluids) break;

            BlockState state = ctx.level().getBlockState(pos);
            FluidState fluidState = state.getFluidState();

            if (!fluidState.isEmpty() && fluidState.isSource()) {
                if (BlockProtectionHelper.tryReplaceBlock(ctx.level(), pos, Blocks.AIR.defaultBlockState(), owner)) {
                    suppressedBlocks.put(pos.immutable(), state);
                    fluidsSuppressed++;
                    final BlockPos suppressedAt = pos.immutable();
                    RitualHelper.chanceStream(ctx.level(), 25, () ->
                            StreamPresets.voidTendril(suppressedAt, ctx.masterPos()).build()
                                    .sendToNearby(ctx.serverLevel(), ctx.masterPos(), 64));
                }
            }
        }

        ctx.syphon(getRefreshCost() * fluidsSuppressed);
    }

    @Override
    public void stopRitual(IMasterRitualStone masterRitualStone, BreakType breakType) {
        Level level = masterRitualStone.getLevel();
        if (level == null || level.isClientSide()) return;

        // Restore all suppressed fluids using lighter update flags to avoid
        // cascading neighbor updates that cause lag with large fluid volumes
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
