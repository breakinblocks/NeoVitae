package com.breakinblocks.neovitae.common.blockentity;

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
import com.breakinblocks.neovitae.spiritus.WorldSpiritusHandler;

/**
 * Tile entity for demon crystals.
 * Handles crystal growth from will aura.
 *
 * <p>AGE in blockstate represents number of crystals (age 0 = 1 crystal, max configurable).</p>
 * <p>Growth rates and conversion values are configurable via server config.</p>
 */
public class SpiritusCrystalBlockEntity extends BaseBlockEntity {

    private static double getSameSpiritusConversionRate() {
        return NeoVitae.SERVER_CONFIG.CRYSTAL_SAME_SPIRITUS_RATE.get();
    }

    private static double getDifferentSpiritusConversionRate() {
        return NeoVitae.SERVER_CONFIG.CRYSTAL_DIFFERENT_SPIRITUS_RATE.get();
    }

    private static double getWrongSpiritusDelay() {
        return NeoVitae.SERVER_CONFIG.CRYSTAL_WRONG_SPIRITUS_DELAY.get();
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

    public double injectedSpiritus = 0;
    public double speedModifier = 1;
    public double appliedConversionRate = 0; // 0 means use default from config

    public SpiritusType spiritusType;

    public SpiritusCrystalBlockEntity(BlockPos pos, BlockState state) {
        this(SpiritusType.RAW, pos, state);
    }

    public SpiritusCrystalBlockEntity(SpiritusType spiritusType, BlockPos pos, BlockState state) {
        super(NVTiles.SPIRITUS_CRYSTAL_TYPE.get(), pos, state);
        this.spiritusType = spiritusType;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SpiritusCrystalBlockEntity tile) {
        if (level.isClientSide) {
            return;
        }

        tile.internalCounter++;

        if (tile.internalCounter % 20 == 0) {
            int crystalCount = tile.getCrystalCount();
            int maxCrystals = getMaxCrystalCount();
            if (crystalCount < maxCrystals) {
                SpiritusType type = tile.getSpiritusType();

                double value = WorldSpiritusHandler.getCurrentSpiritus(level, pos, type);
                double sameRate = getSameSpiritusConversionRate();
                double appliedRate = tile.appliedConversionRate > 0 ? tile.appliedConversionRate : sameRate;

                if (value >= 0.5) {
                    double nextProgress = tile.getCrystalGrowthPerSecond(value);

                    double bufferDrainRate = (sameRate - appliedRate);
                    double conversionRate = Math.min(appliedRate, sameRate);

                    if (tile.injectedSpiritus > 0 && bufferDrainRate > 0) {
                        nextProgress = Math.min(tile.injectedSpiritus / bufferDrainRate, nextProgress);
                    }

                    double spiritusToDrain = nextProgress * conversionRate;
                    double drained = WorldSpiritusHandler.drainSpiritusFromChunk(level, pos, type, spiritusToDrain);
                    nextProgress = Math.min(drained / conversionRate, nextProgress);
                    tile.progressToNextCrystal += nextProgress;

                    if (tile.injectedSpiritus > 0 && bufferDrainRate > 0) {
                        tile.injectedSpiritus = Math.max(0, tile.injectedSpiritus - nextProgress * bufferDrainRate);
                        if (tile.injectedSpiritus <= 0) {
                            tile.appliedConversionRate = 0; // Reset to use config default
                            tile.speedModifier = 1;
                        }
                    }
                } else if (type != SpiritusType.RAW) {
                    // Try using DEFAULT will if own type is not available
                    value = WorldSpiritusHandler.getCurrentSpiritus(level, pos, SpiritusType.RAW);
                    if (value > 0.5) {
                        double differentRate = getDifferentSpiritusConversionRate();
                        double nextProgress = tile.getCrystalGrowthPerSecond(value) * getWrongSpiritusDelay();
                        tile.progressToNextCrystal += WorldSpiritusHandler.drainSpiritusFromChunk(level, pos,
                                SpiritusType.RAW, nextProgress * differentRate)
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
        injectedSpiritus += addedInjectedWill;
    }

    public double growCrystalWithSpiritusAmount(double willDrain, double progressPercentage) {
        int crystalCount = getCrystalCount();
        if (crystalCount >= getMaxCrystalCount()) {
            return 0;
        }

        SpiritusType type = this.getSpiritusType();
        double value = WorldSpiritusHandler.getCurrentSpiritus(getLevel(), worldPosition, type);
        double percentDrain = willDrain <= 0 ? 1 : Math.min(1, value / willDrain);
        if (percentDrain <= 0) {
            return 0;
        }

        WorldSpiritusHandler.drainSpiritusFromChunk(getLevel(), worldPosition, type, percentDrain * willDrain);
        progressToNextCrystal += percentDrain * progressPercentage;

        checkAndGrowCrystal();

        return percentDrain * progressPercentage;
    }

    public SpiritusType getSpiritusType() {
        return spiritusType;
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
     * Calculate growth rate based on available spiritus.
     * Formula: (1.0 / threshold) * sqrt(spiritus / threshold) * growthSpeed * speedModifier * ritualGrowthMult
     */
    public double getCrystalGrowthPerSecond(double spiritus) {
        double threshold = getGrowthThreshold();
        double baseSpeed = 1.0 / threshold * Math.sqrt(spiritus / threshold);
        double speed = baseSpeed * getGrowthSpeed();
        if (speedModifier > 0) {
            speed *= speedModifier;
        }
        if (getLevel() != null) {
            speed *= WorldSpiritusHandler.getSpiritusChunk(getLevel(), getBlockPos()).getGrowthMultiplier();
        }
        return speed;
    }

    public boolean dropSingleCrystal() {
        int crystalCount = getCrystalCount();
        if (!getLevel().isClientSide && crystalCount > 1) {
            SpiritusType type = getSpiritusType();
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
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("placement", placement.get3DDataValue());
        tag.putDouble("progress", progressToNextCrystal);
        tag.putString("spiritusType", spiritusType.getSerializedName());
        tag.putDouble("injectedSpiritus", injectedSpiritus);
        tag.putDouble("speedModifier", speedModifier);
        tag.putDouble("appliedRate", appliedConversionRate);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        placement = Direction.from3DDataValue(tag.getInt("placement"));
        progressToNextCrystal = tag.getDouble("progress");

        if (tag.contains("spiritusType")) {
            String typeStr = tag.getString("spiritusType");
            try {
                spiritusType = SpiritusType.valueOf(typeStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                spiritusType = SpiritusType.RAW;
            }
        } else {
            spiritusType = SpiritusType.RAW;
        }

        injectedSpiritus = tag.getDouble("injectedSpiritus");
        speedModifier = tag.getDouble("speedModifier");
        appliedConversionRate = tag.getDouble("appliedRate");

        if (speedModifier <= 0) {
            speedModifier = 1;
        }
        // appliedConversionRate of 0 means "use config default"
    }
}
