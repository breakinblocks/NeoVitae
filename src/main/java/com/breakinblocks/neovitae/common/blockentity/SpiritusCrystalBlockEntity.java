package com.breakinblocks.neovitae.common.blockentity;


import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.BlockSpiritusCrystal;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.will.WorldSpiritusHandler;

/**
 * Tile entity for demon crystals.
 * Handles crystal growth from will aura.
 *
 * <p>AGE in blockstate represents number of crystals (age 0 = 1 crystal, max configurable).</p>
 * <p>Growth rates and conversion values are configurable via server config.</p>
 */
public class SpiritusCrystalBlockEntity extends BaseBlockEntity {

    private static double getSameWillConversionRate() {
        return NeoVitae.SERVER_CONFIG.CRYSTAL_SAME_WILL_RATE.get();
    }

    private static double getDifferentWillConversionRate() {
        return NeoVitae.SERVER_CONFIG.CRYSTAL_DIFFERENT_WILL_RATE.get();
    }

    private static double getWrongWillDelay() {
        return NeoVitae.SERVER_CONFIG.CRYSTAL_WRONG_WILL_DELAY.get();
    }

    private static double getGrowthSpeed() {
        return NeoVitae.SERVER_CONFIG.CRYSTAL_GROWTH_SPEED.get();
    }

    private static double getGrowthThreshold() {
        return NeoVitae.SERVER_CONFIG.CRYSTAL_GROWTH_THRESHOLD.get();
    }

    private static int getMaxCrystalCount() {
        return NeoVitae.SERVER_CONFIG.CRYSTAL_MAX_COUNT.get();
    }

    public double progressToNextCrystal = 0;
    public int internalCounter = 0;
    public Direction placement = Direction.UP;

    public double injectedWill = 0;
    public double speedModifier = 1;
    public double appliedConversionRate = 0; // 0 means use default from config

    public SpiritusType willType;

    public SpiritusCrystalBlockEntity(BlockPos pos, BlockState state) {
        this(SpiritusType.DEFAULT, pos, state);
    }

    public SpiritusCrystalBlockEntity(SpiritusType willType, BlockPos pos, BlockState state) {
        super(NVTiles.SPIRITUS_CRYSTAL_TYPE.get(), pos, state);
        this.willType = willType;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SpiritusCrystalBlockEntity tile) {
        if (level.isClientSide()) {
            return;
        }

        tile.internalCounter++;

        if (tile.internalCounter % 20 == 0) {
            int crystalCount = tile.getCrystalCount();
            int maxCrystals = getMaxCrystalCount();
            if (crystalCount < maxCrystals) {
                SpiritusType type = tile.getWillType();

                double value = WorldSpiritusHandler.getCurrentWill(level, pos, type);
                double sameRate = getSameWillConversionRate();
                double appliedRate = tile.appliedConversionRate > 0 ? tile.appliedConversionRate : sameRate;

                if (value >= 0.5) {
                    double nextProgress = tile.getCrystalGrowthPerSecond(value);

                    double bufferDrainRate = (sameRate - appliedRate);
                    double conversionRate = Math.min(appliedRate, sameRate);

                    if (tile.injectedWill > 0 && bufferDrainRate > 0) {
                        nextProgress = Math.min(tile.injectedWill / bufferDrainRate, nextProgress);
                    }

                    double willToDrain = nextProgress * conversionRate;
                    double drained = WorldSpiritusHandler.drainWillFromChunk(level, pos, type, willToDrain);
                    nextProgress = Math.min(drained / conversionRate, nextProgress);
                    tile.progressToNextCrystal += nextProgress;

                    if (tile.injectedWill > 0 && bufferDrainRate > 0) {
                        tile.injectedWill = Math.max(0, tile.injectedWill - nextProgress * bufferDrainRate);
                        if (tile.injectedWill <= 0) {
                            tile.appliedConversionRate = 0; // Reset to use config default
                            tile.speedModifier = 1;
                        }
                    }
                } else if (type != SpiritusType.DEFAULT) {
                    // Try using DEFAULT will if own type is not available
                    value = WorldSpiritusHandler.getCurrentWill(level, pos, SpiritusType.DEFAULT);
                    if (value > 0.5) {
                        double differentRate = getDifferentWillConversionRate();
                        double nextProgress = tile.getCrystalGrowthPerSecond(value) * getWrongWillDelay();
                        tile.progressToNextCrystal += WorldSpiritusHandler.drainWillFromChunk(level, pos,
                                SpiritusType.DEFAULT, nextProgress * differentRate)
                                / differentRate;
                    }
                }

                if (tile.speedModifier <= 0) {
                    tile.speedModifier = 1;
                }

                tile.checkAndGrowCrystal();
            }
        }
    }

    public void applyCatalyst(double addedInjectedWill, double speedModifier, double conversionRate) {
        if (this.speedModifier < speedModifier) {
            this.speedModifier = speedModifier;
        }
        if (this.appliedConversionRate > conversionRate) {
            this.appliedConversionRate = conversionRate;
        }
        injectedWill += addedInjectedWill;
    }

    public double growCrystalWithWillAmount(double willDrain, double progressPercentage) {
        int crystalCount = getCrystalCount();
        if (crystalCount >= getMaxCrystalCount()) {
            return 0;
        }

        SpiritusType type = this.getWillType();
        double value = WorldSpiritusHandler.getCurrentWill(getLevel(), worldPosition, type);
        double percentDrain = willDrain <= 0 ? 1 : Math.min(1, value / willDrain);
        if (percentDrain <= 0) {
            return 0;
        }

        WorldSpiritusHandler.drainWillFromChunk(getLevel(), worldPosition, type, percentDrain * willDrain);
        progressToNextCrystal += percentDrain * progressPercentage;

        checkAndGrowCrystal();

        return percentDrain * progressPercentage;
    }

    public SpiritusType getWillType() {
        return willType;
    }

    public void checkAndGrowCrystal() {
        int crystalCount = getCrystalCount();
        int maxCrystals = getMaxCrystalCount();
        if (progressToNextCrystal >= 1 && internalCounter % 100 == 0 && crystalCount < maxCrystals) {
            progressToNextCrystal--;
            setCrystalCount(crystalCount + 1);
            setChanged();
        }
    }

    /**
     * Calculate growth rate based on available will.
     * Formula: (1.0 / threshold) * sqrt(will / threshold) * growthSpeed * speedModifier
     */
    public double getCrystalGrowthPerSecond(double will) {
        double threshold = getGrowthThreshold();
        double baseSpeed = 1.0 / threshold * Math.sqrt(will / threshold);
        double speed = baseSpeed * getGrowthSpeed();
        if (speedModifier > 0) {
            speed *= speedModifier;
        }
        return speed;
    }

    public boolean dropSingleCrystal() {
        int crystalCount = getCrystalCount();
        if (!getLevel().isClientSide() && crystalCount > 1) {
            SpiritusType type = getWillType();
            ItemStack stack = BlockSpiritusCrystal.getItemStackDropped(type, 1);
            if (!stack.isEmpty()) {
                setCrystalCount(crystalCount - 1);
                Containers.dropItemStack(getLevel(), worldPosition.getX(), worldPosition.getY(),
                        worldPosition.getZ(), stack);
                setChanged();
                return true;
            }
        }
        return false;
    }

    public int getCrystalCount() {
        BlockState state = getLevel().getBlockState(getBlockPos());
        if (state.hasProperty(BlockSpiritusCrystal.AGE)) {
            return state.getValue(BlockSpiritusCrystal.AGE) + 1;
        }
        return 1;
    }

    public void setCrystalCount(int crystalCount) {
        BlockState state = getLevel().getBlockState(getBlockPos());
        if (state.hasProperty(BlockSpiritusCrystal.AGE)) {
            getLevel().setBlockAndUpdate(getBlockPos(),
                    state.setValue(BlockSpiritusCrystal.AGE, Math.max(0, Math.min(6, crystalCount - 1))));
        }
    }

    @Override
    protected void saveAdditional(ValueOutput tag) {
        super.saveAdditional(tag);
        tag.putInt("placement", placement.get3DDataValue());
        tag.putDouble("progress", progressToNextCrystal);
        tag.putString("willType", willType.getSerializedName());
        tag.putDouble("injectedWill", injectedWill);
        tag.putDouble("speedModifier", speedModifier);
        tag.putDouble("appliedRate", appliedConversionRate);
    }

    @Override
    protected void loadAdditional(ValueInput tag) {
        super.loadAdditional(tag);
        placement = Direction.from3DDataValue(tag.getIntOr("placement", 0));
        progressToNextCrystal = tag.getDoubleOr("progress", 0d);

        String typeStr = tag.getStringOr("willType", "");
        if (typeStr.isEmpty()) {
            willType = SpiritusType.DEFAULT;
        } else {
            try {
                willType = SpiritusType.valueOf(typeStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                willType = SpiritusType.DEFAULT;
            }
        }

        injectedWill = tag.getDoubleOr("injectedWill", 0d);
        speedModifier = tag.getDoubleOr("speedModifier", 0d);
        appliedConversionRate = tag.getDoubleOr("appliedRate", 0d);

        if (speedModifier <= 0) {
            speedModifier = 1;
        }
        // appliedConversionRate of 0 means "use config default"
    }
}
