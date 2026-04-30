package com.breakinblocks.neovitae.common.blockentity;


import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.block.BlockSpiritusCrystal;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.will.WorldSpiritusHandler;

/**
 * Crystallarium Maleficum - forms the initial demon crystal from will aura.
 *
 * <p>The crystallizer's only job is to form the FIRST crystal when there's air above it.
 * All further crystal growth is handled by the SpiritusCrystalBlockEntity itself.</p>
 *
 * <p>Configurable via server config (default: 99 will to form, 1000 ticks formation time).</p>
 */
public class CrystallariumMaleficumBlockEntity extends BaseBlockEntity {

    private static double getWillToFormCrystal() {
        return NeoVitae.SERVER_CONFIG.CRYSTAL_SPIRITUS_TO_FORM.get();
    }

    private static double getTotalFormationTime() {
        return NeoVitae.SERVER_CONFIG.CRYSTAL_FORMATION_TIME.get();
    }

    private double internalCounter = 0;

    public CrystallariumMaleficumBlockEntity(BlockPos pos, BlockState state) {
        super(NVTiles.CRYSTALLARIUM_MALEFICUM_TYPE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CrystallariumMaleficumBlockEntity tile) {
        if (level.isClientSide()) {
            return;
        }

        tile.onUpdate();
    }

    private void onUpdate() {
        BlockPos offsetPos = worldPosition.above();

        // Only form a crystal when there's air above - crystal growth is handled by the crystal itself
        if (level.isEmptyBlock(offsetPos)) {
            SpiritusType highestType = WorldSpiritusHandler.getDominantWillType(level, worldPosition);
            double amount = WorldSpiritusHandler.getCurrentWill(level, worldPosition, highestType);

            double willToForm = getWillToFormCrystal();
            if (amount >= willToForm) {
                double formationRate = getCrystalFormationRate(amount);
                internalCounter += formationRate;

                if (internalCounter >= getTotalFormationTime()) {
                    double drained = WorldSpiritusHandler.drainWillFromChunk(level, worldPosition, highestType, willToForm);
                    if (drained >= willToForm) {
                        if (formCrystal(highestType, offsetPos)) {
                            internalCounter = 0;
                            setChanged();
                        }
                    }
                }
            }
        } else {
            if (internalCounter > 0) {
                internalCounter = 0;
                setChanged();
            }
        }
    }

    private boolean formCrystal(SpiritusType type, BlockPos position) {
        Block block = switch (type) {
            case RUINA -> NVBlocks.SPIRITUS_RUINA_CRYSTAL.block().get();
            case NIHILUM -> NVBlocks.SPIRITUS_NIHILUM_CRYSTAL.block().get();
            case VINDICTA -> NVBlocks.SPIRITUS_VINDICTA_CRYSTAL.block().get();
            case INVICTUS -> NVBlocks.SPIRITUS_INVICTUS_CRYSTAL.block().get();
            default -> NVBlocks.RAW_SPIRITUS_CRYSTAL.block().get();
        };

        BlockState crystalState = block.defaultBlockState()
                .setValue(BlockSpiritusCrystal.AGE, 0)
                .setValue(BlockSpiritusCrystal.ATTACHED, Direction.UP);

        level.setBlock(position, crystalState, Block.UPDATE_ALL);

        BlockEntity tile = level.getBlockEntity(position);
        if (tile instanceof SpiritusCrystalBlockEntity crystalTile) {
            crystalTile.placement = Direction.UP;
            crystalTile.setChanged();
            return true;
        }

        return false;
    }

    private double getCrystalFormationRate(double currentSpiritus) {
        return 1.0;
    }

    public double getInternalCounter() {
        return internalCounter;
    }

    @Override
    protected void saveAdditional(ValueOutput tag) {
        super.saveAdditional(tag);
        tag.putDouble("internalCounter", internalCounter);
    }

    @Override
    protected void loadAdditional(ValueInput tag) {
        super.loadAdditional(tag);
        internalCounter = tag.getDoubleOr("internalCounter", 0d);
    }
}
