package com.breakinblocks.neovitae.common.blockentity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
            List<ResourceLocation> potentialRoomTypes
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
                        ResourceLocation.CODEC.listOf().fieldOf("potentialRoomTypes").forGetter(SealData::potentialRoomTypes)
                ).apply(instance, SealData::new)
        );
    }

    private SealData data = SealData.EMPTY;

    public DungeonSealBlockEntity(BlockPos pos, BlockState state) {
        super(NVTiles.DUNGEON_SEAL_TYPE.get(), pos, state);
    }

    public SealData getData() { return data; }

    public void initialize(BlockPos controllerPos, BlockPos doorPos, Direction doorDirection,
                           String doorType, List<ResourceLocation> potentialRoomTypes) {
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

    public List<ResourceLocation> getPotentialRoomTypes() {
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
        ResourceLocation[] roomTypes = data.potentialRoomTypes().toArray(new ResourceLocation[0]);

        boolean success = controller.handleRequestForRoomPlacement(
                worldPosition, data.doorPos(), data.doorDirection(), data.doorType(), roomTypes, rand);

        if (success) {
            controller.getDungeonSynthesizer().decrementSealCount();
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

        ResourceLocation selectedRoom = key.getValidResourceLocation(new ArrayList<>(data.potentialRoomTypes()));
        if (selectedRoom == null) {
            LOGGER.debug("Key {} didn't match any room types for seal at {}", key.getKeyType(), worldPosition);
            return false;
        }

        RandomSource rand = serverLevel.getRandom();
        ResourceLocation[] roomTypes = new ResourceLocation[] { selectedRoom };

        boolean success = controller.handleRequestForRoomPlacement(
                worldPosition, data.doorPos(), data.doorDirection(), data.doorType(), roomTypes, rand);

        if (success) {
            controller.getDungeonSynthesizer().decrementSealCount();
            serverLevel.removeBlock(worldPosition, false);
            return true;
        }

        return false;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        SealData.CODEC.encodeStart(NbtOps.INSTANCE, data)
                .resultOrPartial(LOGGER::error)
                .ifPresent(nbt -> tag.put("sealData", nbt));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        if (tag.contains("sealData")) {
            data = SealData.CODEC.parse(NbtOps.INSTANCE, tag.get("sealData"))
                    .resultOrPartial(LOGGER::error)
                    .orElse(SealData.EMPTY);
        }
    }
}
