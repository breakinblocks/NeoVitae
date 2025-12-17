package com.breakinblocks.neovitae.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.BMBlocks;
import com.breakinblocks.neovitae.common.block.BlockDemonCrystal;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;
import com.breakinblocks.neovitae.will.WorldDemonWillHandler;

/**
 * Demon Crystallizer - forms the initial demon crystal from will aura.
 *
 * <p>The crystallizer's only job is to form the FIRST crystal when there's air above it.
 * All further crystal growth is handled by the DemonCrystalTile itself.</p>
 *
 * <p>Configurable via server config (default: 99 will to form, 1000 ticks formation time).</p>
 */
public class DemonCrystallizerTile extends BaseTile {

    /**
     * Gets the configured will required to form a crystal.
     */
    private static double getWillToFormCrystal() {
        return NeoVitae.SERVER_CONFIG.CRYSTAL_WILL_TO_FORM.get();
    }

    /**
     * Gets the configured total formation time in ticks.
     */
    private static double getTotalFormationTime() {
        return NeoVitae.SERVER_CONFIG.CRYSTAL_FORMATION_TIME.get();
    }

    private double internalCounter = 0;

    public DemonCrystallizerTile(BlockPos pos, BlockState state) {
        super(BMTiles.DEMON_CRYSTALLIZER_TYPE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, DemonCrystallizerTile tile) {
        if (level.isClientSide()) {
            return;
        }

        tile.onUpdate();
    }

    private void onUpdate() {
        BlockPos offsetPos = worldPosition.above();

        // Only form a crystal when there's air above - crystal growth is handled by the crystal itself
        if (level.isEmptyBlock(offsetPos)) {
            EnumWillType highestType = WorldDemonWillHandler.getDominantWillType(level, worldPosition);
            double amount = WorldDemonWillHandler.getCurrentWill(level, worldPosition, highestType);

            double willToForm = getWillToFormCrystal();
            if (amount >= willToForm) {
                double formationRate = getCrystalFormationRate(amount);
                internalCounter += formationRate;

                if (internalCounter >= getTotalFormationTime()) {
                    // Try to drain will and form crystal
                    double drained = WorldDemonWillHandler.drainWillFromChunk(level, worldPosition, highestType, willToForm);
                    if (drained >= willToForm) {
                        if (formCrystal(highestType, offsetPos)) {
                            internalCounter = 0;
                            setChanged();
                        }
                    }
                }
            }
        } else {
            // Reset counter if there's something above
            if (internalCounter > 0) {
                internalCounter = 0;
                setChanged();
            }
        }
    }

    /**
     * Forms a crystal of the given type at the given position.
     * @return true if successful
     */
    private boolean formCrystal(EnumWillType type, BlockPos position) {
        Block block = switch (type) {
            case CORROSIVE -> BMBlocks.CORROSIVE_DEMON_CRYSTAL.block().get();
            case DESTRUCTIVE -> BMBlocks.DESTRUCTIVE_DEMON_CRYSTAL.block().get();
            case VENGEFUL -> BMBlocks.VENGEFUL_DEMON_CRYSTAL.block().get();
            case STEADFAST -> BMBlocks.STEADFAST_DEMON_CRYSTAL.block().get();
            default -> BMBlocks.RAW_DEMON_CRYSTAL.block().get();
        };

        // Place crystal with AGE 0 (1 crystal) and attached to UP direction
        BlockState crystalState = block.defaultBlockState()
                .setValue(BlockDemonCrystal.AGE, 0)
                .setValue(BlockDemonCrystal.ATTACHED, Direction.UP);

        level.setBlock(position, crystalState, Block.UPDATE_ALL);

        // Set up the tile entity
        BlockEntity tile = level.getBlockEntity(position);
        if (tile instanceof DemonCrystalTile crystalTile) {
            crystalTile.placement = Direction.UP;
            crystalTile.setChanged();
            return true;
        }

        return false;
    }

    /**
     * Get the formation rate based on available will.
     * Currently returns 1, but can be modified for different formation speeds.
     */
    private double getCrystalFormationRate(double currentWill) {
        return 1.0;
    }

    public double getInternalCounter() {
        return internalCounter;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putDouble("internalCounter", internalCounter);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        internalCounter = tag.getDouble("internalCounter");
    }
}
