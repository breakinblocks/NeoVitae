package com.breakinblocks.neovitae.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import com.breakinblocks.neovitae.common.NVSounds;
import com.breakinblocks.neovitae.incense.EnumTranquilityType;
import com.breakinblocks.neovitae.incense.IncenseAltarHandler;
import com.breakinblocks.neovitae.incense.IncenseHelper;
import com.breakinblocks.neovitae.incense.TranquilityRegistry;
import com.breakinblocks.neovitae.incense.TranquilityStack;
import net.minecraft.sounds.SoundSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Block entity for the Incense Altar.
 * Periodically scans for road rings and calculates tranquility bonus.
 * Players within range accumulate incense, which boosts self-sacrifice.
 */
public class IncenseAltarBlockEntity extends BlockEntity {
    public static final int MAX_CHECK_RANGE = 5; // Vertical range to check for roads
    public static final int MAX_ROAD_DISTANCE = 12; // Maximum road ring distance

    private Map<EnumTranquilityType, Double> tranquilityMap = new HashMap<>();

    private double incenseAddition = 0; // Self-sacrifice is multiplied by (1 + this value)
    private double tranquility = 0;
    private int roadDistance = 0; // Number of road rings found

    public IncenseAltarBlockEntity(BlockPos pos, BlockState state) {
        super(NVTiles.INCENSE_ALTAR_TYPE.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, IncenseAltarBlockEntity tile) {
        AABB playerArea = new AABB(pos).inflate(5, 5, 5);
        List<Player> players = level.getEntitiesOfClass(Player.class, playerArea);

        if (players.isEmpty()) {
            return;
        }

        if (level.getGameTime() % 100 == 0) {
            tile.recheckConstruction();
        }

        boolean hasPerformed = false;

        for (Player player : players) {
            if (IncenseHelper.incrementIncense(player, 0, tile.incenseAddition, tile.incenseAddition / 100.0)) {
                hasPerformed = true;
            }
        }

        if (hasPerformed && level.random.nextInt(4) == 0 && level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.FLAME,
                    pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5,
                    1, 0.02, 0.03, 0.02, 0);
        }
    }

    public void recheckConstruction() {
        int maxLength = MAX_ROAD_DISTANCE - 1; // Max path rings to check
        int yOffset = 0;

        Map<EnumTranquilityType, Double> newTranquilityMap = new HashMap<>();
        int foundRoads = 0;

        for (int currentDistance = 2; currentDistance <= 2 + maxLength; currentDistance++) {
            boolean canFormRoad = false;

            // Check different Y levels to find roads
            for (int i = -MAX_CHECK_RANGE + yOffset; i <= MAX_CHECK_RANGE + yOffset; i++) {
                BlockPos verticalPos = worldPosition.offset(0, i, 0);

                canFormRoad = true;
                directionLoop:
                for (int dirIndex = 0; dirIndex < 4; dirIndex++) {
                    Direction horizontalFacing = Direction.from2DDataValue(dirIndex);
                    BlockPos facingOffsetPos = verticalPos.relative(horizontalFacing, currentDistance);

                    for (int j = -1; j <= 1; j++) {
                        BlockPos offsetPos = facingOffsetPos.relative(horizontalFacing.getClockWise(), j);
                        BlockState state = level.getBlockState(offsetPos);

                        int pathLevel = TranquilityRegistry.getPathLevel(state);
                        int requiredLevel = currentDistance - 2; // Level 0 for distance 2, etc.

                        if (pathLevel < requiredLevel) {
                            canFormRoad = false;
                            break directionLoop;
                        }
                    }
                }

                if (canFormRoad) {
                    yOffset = i;
                    break;
                }
            }

            if (canFormRoad) {
                foundRoads++;

                for (int i = -currentDistance; i <= currentDistance; i++) {
                    for (int j = -currentDistance; j <= currentDistance; j++) {
                        if (Math.abs(i) != currentDistance && Math.abs(j) != currentDistance) {
                            continue;
                        }

                        for (int y = yOffset; y <= 2 + yOffset; y++) {
                            BlockPos offsetPos = worldPosition.offset(i, y, j);
                            BlockState state = level.getBlockState(offsetPos);

                            TranquilityStack stack = TranquilityRegistry.getTranquility(level, offsetPos, state);
                            if (stack != null) {
                                newTranquilityMap.merge(stack.type, stack.value, Double::sum);
                            }
                        }
                    }
                }
            } else {
                break;
            }
        }

        this.roadDistance = foundRoads;
        this.tranquilityMap = newTranquilityMap;

        double totalTranquility = 0;
        for (Double value : tranquilityMap.values()) {
            totalTranquility += value;
        }

        if (totalTranquility < 0) {
            return;
        }

        double appliedTranquility = 0;
        for (Double value : tranquilityMap.values()) {
            appliedTranquility += Math.sqrt(value);
        }

        double bonus = IncenseAltarHandler.getIncenseBonus(appliedTranquility, roadDistance);
        if (this.incenseAddition <= 0 && bonus > 0 && level != null) {
            level.playSound(null, worldPosition, NVSounds.INCENSE_IGNITE.get(), SoundSource.BLOCKS, 0.4f, 1.0f);
        }
        this.incenseAddition = bonus;
        this.tranquility = appliedTranquility;

        setChanged();
    }

    public double getIncenseAddition() {
        return incenseAddition;
    }

    public double getTranquility() {
        return tranquility;
    }

    public int getRoadDistance() {
        return roadDistance;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        tranquility = tag.getDouble("tranquility");
        incenseAddition = tag.getDouble("incenseAddition");
        roadDistance = tag.getInt("roadDistance");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putDouble("tranquility", tranquility);
        tag.putDouble("incenseAddition", incenseAddition);
        tag.putInt("roadDistance", roadDistance);
    }

}
