package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
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
                // Only suppress source blocks (with protection check)
                if (BlockProtectionHelper.tryReplaceBlock(ctx.level(), pos, Blocks.AIR.defaultBlockState(), owner)) {
                    suppressedBlocks.put(pos.immutable(), state);
                    fluidsSuppressed++;
                }
            }
        }

        ctx.syphon(getRefreshCost() * fluidsSuppressed);
    }

    @Override
    public void stopRitual(IMasterRitualStone masterRitualStone, BreakType breakType) {
        Level level = masterRitualStone.getLevel();
        if (level == null || level.isClientSide()) return;

        // Restore all suppressed fluids
        for (Map.Entry<BlockPos, BlockState> entry : suppressedBlocks.entrySet()) {
            BlockPos pos = entry.getKey();
            BlockState state = entry.getValue();
            if (level.getBlockState(pos).isAir()) {
                level.setBlock(pos, state, 3);
            }
        }
        suppressedBlocks.clear();
    }

    @Override
    public int getRefreshTime() {
        return 10;
    }

    @Override
    public int getRefreshCost() {
        return 5;
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
        ListTag blockList = new ListTag();
        for (Map.Entry<BlockPos, BlockState> entry : suppressedBlocks.entrySet()) {
            CompoundTag blockTag = new CompoundTag();
            blockTag.put("pos", NbtUtils.writeBlockPos(entry.getKey()));
            blockTag.put("state", NbtUtils.writeBlockState(entry.getValue()));
            blockList.add(blockTag);
        }
        tag.put("suppressedBlocks", blockList);
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        suppressedBlocks.clear();
        if (tag.contains("suppressedBlocks")) {
            ListTag blockList = tag.getList("suppressedBlocks", Tag.TAG_COMPOUND);
            for (int i = 0; i < blockList.size(); i++) {
                CompoundTag blockTag = blockList.getCompound(i);
                NbtUtils.readBlockPos(blockTag, "pos").ifPresent(pos -> {
                    BlockState state = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), blockTag.getCompound("state"));
                    if (!state.isAir()) {
                        suppressedBlocks.put(pos, state);
                    }
                });
            }
        }
    }
}
