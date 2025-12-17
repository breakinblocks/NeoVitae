package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.BMBlocks;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * Resonance of the Faceted Crystal - Splits raw demon will crystals into aspected crystals.
 * Takes a raw crystal 2 blocks above the MRS and creates 4 aspected crystals above elemental runes.
 * This is a Dusk tier ritual.
 */
public class RitualCrystalSplit extends Ritual {

    public RitualCrystalSplit() {
        super("crystal_split", 1, 50000, "ritual." + NeoVitae.MODID + ".crystal_split");
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) {
            masterRitualStone.stopRitual(BreakType.DEACTIVATE);
            return;
        }

        // Check for raw crystal block 2 blocks above
        BlockPos crystalPos = ctx.masterPos().above(2);
        BlockState crystalState = ctx.level().getBlockState(crystalPos);

        if (!crystalState.is(BMBlocks.RAW_DEMON_CRYSTAL.block().get())) {
            // No raw crystal to split
            return;
        }

        // Define positions for the 4 aspected crystals (above the 4 elemental runes)
        BlockPos[] outputPositions = {
            ctx.masterPos().north(3).above(2), // Corrosive
            ctx.masterPos().south(3).above(2), // Destructive
            ctx.masterPos().east(3).above(2),  // Vengeful
            ctx.masterPos().west(3).above(2)   // Steadfast
        };

        Block[] aspectedCrystals = {
            BMBlocks.CORROSIVE_DEMON_CRYSTAL.block().get(),
            BMBlocks.DESTRUCTIVE_DEMON_CRYSTAL.block().get(),
            BMBlocks.VENGEFUL_DEMON_CRYSTAL.block().get(),
            BMBlocks.STEADFAST_DEMON_CRYSTAL.block().get()
        };

        // Check if all output positions are empty
        for (BlockPos pos : outputPositions) {
            if (!ctx.level().isEmptyBlock(pos)) {
                return; // Can't place, position occupied
            }
        }

        UUID owner = ctx.master().getOwner();

        // Remove the raw crystal (with protection check)
        if (!BlockProtectionHelper.tryRemoveBlock(ctx.level(), crystalPos, owner)) {
            return;
        }

        // Place the 4 aspected crystals (with protection checks)
        for (int i = 0; i < 4; i++) {
            BlockProtectionHelper.tryPlaceBlock(ctx.level(), outputPositions[i], aspectedCrystals[i].defaultBlockState(), owner);
        }

        // Consume LP and deactivate (one-time ritual)
        ctx.syphon(getRefreshCost());
        masterRitualStone.stopRitual(BreakType.DEACTIVATE);
    }

    @Override
    public int getRefreshTime() {
        return 20;
    }

    @Override
    public int getRefreshCost() {
        return 25000;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        // Four elemental runes at cardinal directions
        components.accept(new RitualComponent(new BlockPos(3, 0, 0), EnumRuneType.FIRE));
        components.accept(new RitualComponent(new BlockPos(-3, 0, 0), EnumRuneType.WATER));
        components.accept(new RitualComponent(new BlockPos(0, 0, 3), EnumRuneType.EARTH));
        components.accept(new RitualComponent(new BlockPos(0, 0, -3), EnumRuneType.AIR));

        // Dusk runes at corners
        addCornerRunes(components, 2, 0, EnumRuneType.DUSK);

        // Connect the pattern
        addParallelRunes(components, 2, 0, EnumRuneType.EARTH);
        addCornerRunes(components, 3, 0, EnumRuneType.FIRE);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualCrystalSplit();
    }
}
