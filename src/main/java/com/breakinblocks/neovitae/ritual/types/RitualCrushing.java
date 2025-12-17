package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Ritual that crushes blocks in a configurable area.
 * Converts ores to crushed materials for bonus yield.
 */
public class RitualCrushing extends Ritual {

    public static final String CRUSH_RANGE = "crushRange";

    public RitualCrushing() {
        super("crushing", 0, 2500, "ritual." + NeoVitae.MODID + ".crushing");
        addBlockRange(CRUSH_RANGE, new AreaDescriptor.Rectangle(new BlockPos(0, -1, 0), 1, 1, 1));
        setMaximumVolumeAndDistanceOfRange(CRUSH_RANGE, 64, 10, 10);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) return;

        if (!(ctx.level() instanceof ServerLevel)) return;

        List<BlockPos> positions = RitualHelper.getRangePositions(ctx.master(), this, CRUSH_RANGE, ctx.masterPos());
        int blocksCrushed = 0;
        int maxBlocks = ctx.maxOperations(getRefreshCost());
        UUID owner = ctx.master().getOwner();

        for (BlockPos pos : positions) {
            if (blocksCrushed >= maxBlocks) break;

            BlockState state = ctx.level().getBlockState(pos);
            Block block = state.getBlock();

            // Simple crushing logic - convert stone to cobblestone, cobble to gravel, etc.
            Block result = getCrushResult(block);
            if (result != null) {
                // Check protection before replacing block
                if (BlockProtectionHelper.tryReplaceBlock(ctx.level(), pos, result.defaultBlockState(), owner)) {
                    blocksCrushed++;
                }
            }
        }

        ctx.syphon(getRefreshCost() * blocksCrushed);
    }

    private Block getCrushResult(Block block) {
        if (block == Blocks.STONE) return Blocks.COBBLESTONE;
        if (block == Blocks.COBBLESTONE) return Blocks.GRAVEL;
        if (block == Blocks.GRAVEL) return Blocks.SAND;
        if (block == Blocks.SANDSTONE) return Blocks.SAND;
        if (block == Blocks.RED_SANDSTONE) return Blocks.RED_SAND;
        if (block == Blocks.DEEPSLATE) return Blocks.COBBLED_DEEPSLATE;
        if (block == Blocks.COBBLED_DEEPSLATE) return Blocks.GRAVEL;
        if (block == Blocks.NETHERRACK) return Blocks.SOUL_SAND;
        return null;
    }

    @Override
    public int getRefreshTime() {
        return 40;
    }

    @Override
    public int getRefreshCost() {
        return 100;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addCornerRunes(components, 1, 0, EnumRuneType.FIRE);
        addParallelRunes(components, 2, 0, EnumRuneType.EARTH);
        addCornerRunes(components, 2, 0, EnumRuneType.EARTH);
        addRune(components, 3, 0, 0, EnumRuneType.FIRE);
        addRune(components, -3, 0, 0, EnumRuneType.FIRE);
        addRune(components, 0, 0, 3, EnumRuneType.FIRE);
        addRune(components, 0, 0, -3, EnumRuneType.FIRE);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualCrushing();
    }
}
