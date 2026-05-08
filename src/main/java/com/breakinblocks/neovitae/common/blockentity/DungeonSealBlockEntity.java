package com.breakinblocks.neovitae.common.blockentity;


import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonBlocks;
import com.breakinblocks.neovitae.common.item.dungeon.ItemDungeonKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Block entity for the Dungeon Seal block.
 * Represents a sealed door that can be opened to generate new dungeon rooms.
 * Uses Codec-based serialization for modern NeoForge patterns.
 */
public class DungeonSealBlockEntity extends BaseBlockEntity {

    private static final Logger LOGGER = LoggerFactory.getLogger(DungeonSealBlockEntity.class);

    public record SealData(
            BlockPos controllerPos,
            BlockPos doorPos,
            Direction doorDirection,
            String doorType,
            List<Identifier> potentialRoomTypes
    ) {
        public static final SealData EMPTY = new SealData(
                BlockPos.ZERO, BlockPos.ZERO, Direction.NORTH, "", List.of()
        );

        public static final Codec<SealData> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        BlockPos.CODEC.fieldOf("controllerPos").forGetter(SealData::controllerPos),
                        BlockPos.CODEC.fieldOf("doorPos").forGetter(SealData::doorPos),
                        Direction.CODEC.fieldOf("doorDirection").forGetter(SealData::doorDirection),
                        Codec.STRING.fieldOf("doorType").forGetter(SealData::doorType),
                        Identifier.CODEC.listOf().fieldOf("potentialRoomTypes").forGetter(SealData::potentialRoomTypes)
                ).apply(instance, SealData::new)
        );
    }

    private SealData data = SealData.EMPTY;

    public DungeonSealBlockEntity(BlockPos pos, BlockState state) {
        super(NVTiles.DUNGEON_SEAL_TYPE.get(), pos, state);
    }

    public SealData getData() { return data; }

    public void initialize(BlockPos controllerPos, BlockPos doorPos, Direction doorDirection,
                           String doorType, List<Identifier> potentialRoomTypes) {
        this.data = new SealData(controllerPos, doorPos, doorDirection, doorType, List.copyOf(potentialRoomTypes));
        setChanged();
    }

    public BlockPos getControllerPos() {
        return data.controllerPos();
    }

    public BlockPos getDoorPos() {
        return data.doorPos();
    }

    public Direction getDoorDirection() {
        return data.doorDirection();
    }

    public String getDoorType() {
        return data.doorType();
    }

    public List<Identifier> getPotentialRoomTypes() {
        return data.potentialRoomTypes();
    }

    /**
     * Called when a player activates this seal.
     * Requests a new room from the dungeon controller.
     *
     * @param player The player activating the seal
     * @return true if a room was successfully generated
     */
    public boolean requestRoomFromController(Player player) {
        if (level == null || level.isClientSide() || !(level instanceof ServerLevel serverLevel)) {
            return false;
        }

        if (data.controllerPos().equals(BlockPos.ZERO)) {
            LOGGER.warn("Seal at {} has no controller position set", worldPosition);
            return false;
        }

        if (data.potentialRoomTypes().isEmpty()) {
            LOGGER.warn("Seal at {} has no potential room types", worldPosition);
            return false;
        }

        if (!(serverLevel.getBlockEntity(data.controllerPos()) instanceof DungeonControllerBlockEntity controller)) {
            LOGGER.warn("No dungeon controller found at {} for seal at {}", data.controllerPos(), worldPosition);
            return false;
        }

        RandomSource rand = serverLevel.getRandom();
        Identifier[] roomTypes = data.potentialRoomTypes().toArray(new Identifier[0]);

        boolean success = controller.handleRequestForRoomPlacement(
                worldPosition, data.doorPos(), data.doorDirection(), data.doorType(), roomTypes, rand);

        if (success) {
            controller.getDungeonSynthesizer().decrementSealCount();
            clearDoorwayFill(serverLevel);
            serverLevel.removeBlock(worldPosition, false);
            return true;
        }

        controller.queueSealForValidation(worldPosition);
        return false;
    }

    public boolean requestRoomFromControllerWithKey(Player player, ItemDungeonKey key) {
        if (level == null || level.isClientSide() || !(level instanceof ServerLevel serverLevel)) {
            return false;
        }

        if (data.controllerPos().equals(BlockPos.ZERO)) {
            LOGGER.warn("Seal at {} has no controller position set", worldPosition);
            return false;
        }

        if (data.potentialRoomTypes().isEmpty()) {
            LOGGER.warn("Seal at {} has no potential room types", worldPosition);
            return false;
        }

        if (!(serverLevel.getBlockEntity(data.controllerPos()) instanceof DungeonControllerBlockEntity controller)) {
            LOGGER.warn("No dungeon controller found at {} for seal at {}", data.controllerPos(), worldPosition);
            return false;
        }

        Identifier selectedRoom = key.getValidResourceLocation(new ArrayList<>(data.potentialRoomTypes()));
        if (selectedRoom == null) {
            LOGGER.info("[GEN] Seal {} rejected key {} (potentialPools={})",
                    worldPosition, key.getKeyType(), data.potentialRoomTypes());
            return false;
        }

        LOGGER.info("[GEN] Seal {} accepted key {} -> pool {} (doorPos={}, doorDir={}, doorType={}, controller={})",
                worldPosition, key.getKeyType(), selectedRoom,
                data.doorPos(), data.doorDirection(), data.doorType(), data.controllerPos());

        RandomSource rand = serverLevel.getRandom();
        Identifier[] roomTypes = new Identifier[] { selectedRoom };

        boolean success = controller.handleRequestForRoomPlacement(
                worldPosition, data.doorPos(), data.doorDirection(), data.doorType(), roomTypes, rand);

        if (success) {
            controller.getDungeonSynthesizer().decrementSealCount();
            clearDoorwayFill(serverLevel);
            serverLevel.removeBlock(worldPosition, false);
            LOGGER.info("[GEN] Seal {} consumed; new dungeon room placed", worldPosition);
            return true;
        }

        LOGGER.warn("[GEN] Seal {} key-driven placement FAILED for pool {}", worldPosition, selectedRoom);
        return false;
    }

    /**
     * Clears the doorway fill blocks that were placed when this seal was created.
     */
    private void clearDoorwayFill(ServerLevel serverLevel) {
        BlockPos sealPos = worldPosition;
        Direction doorDir = data.doorDirection();
        Direction rightDir = doorDir.getClockWise();

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                BlockPos fillPos = sealPos.relative(rightDir, i).relative(Direction.UP, j);
                if (serverLevel.getBlockState(fillPos).is(
                        DungeonBlocks.DUNGEON_BRICK_ASSORTED.block().get())) {
                    serverLevel.removeBlock(fillPos, false);
                }
            }
        }
    }

    @Override
    protected void saveAdditional(ValueOutput tag) {
        super.saveAdditional(tag);
        tag.store("sealData", SealData.CODEC, data);
    }

    @Override
    protected void loadAdditional(ValueInput tag) {
        super.loadAdditional(tag);
        data = tag.read("sealData", SealData.CODEC).orElse(SealData.EMPTY);
    }
}
