package com.breakinblocks.neovitae.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

/**
 * Block entity for the Inversion Pillar - handles teleportation within the dungeon system.
 */
public class TileInversionPillar extends BaseTile {

    private static final Logger LOGGER = LoggerFactory.getLogger(TileInversionPillar.class);

    @Nullable
    private BlockPos teleportPos;
    @Nullable
    private ResourceLocation destinationKey;

    public TileInversionPillar(BlockPos pos, BlockState state) {
        super(BMTiles.INVERSION_PILLAR_TYPE.get(), pos, state);
    }

    /**
     * Sets the destination using a ResourceLocation for the dimension.
     */
    public void setDestination(BlockPos pos, ResourceLocation dimension) {
        this.teleportPos = pos;
        this.destinationKey = dimension;
        setChanged();
        LOGGER.info("Pillar at {} set destination to {} in {}", worldPosition, pos, dimension);
    }

    /**
     * Sets the destination using a Level reference (convenience method).
     * Extracts the dimension ResourceLocation from the level.
     */
    public void setDestination(Level destinationWorld, BlockPos destinationPos) {
        setDestination(destinationPos, destinationWorld.dimension().location());
    }

    public boolean hasDestination() {
        return teleportPos != null && destinationKey != null;
    }

    @Nullable
    public BlockPos getTeleportPos() {
        return teleportPos;
    }

    @Nullable
    public ResourceLocation getDestinationKey() {
        return destinationKey;
    }

    public void handlePlayerInteraction(Player player) {
        if (level == null || level.isClientSide) return;

        if (!hasDestination()) {
            return;
        }

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        ServerLevel serverLevel = (ServerLevel) level;

        // Get the destination level
        ResourceKey<Level> destKey = ResourceKey.create(Registries.DIMENSION, destinationKey);
        ServerLevel destLevel = serverLevel.getServer().getLevel(destKey);

        if (destLevel == null) {
            LOGGER.warn("Could not find destination dimension: {}", destinationKey);
            return;
        }

        // Teleport the player using the proper API
        serverPlayer.teleportTo(
                destLevel,
                teleportPos.getX() + 0.5,
                teleportPos.getY(),
                teleportPos.getZ() + 0.5,
                player.getYRot(),
                player.getXRot()
        );
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (teleportPos != null) {
            tag.putInt("teleportX", teleportPos.getX());
            tag.putInt("teleportY", teleportPos.getY());
            tag.putInt("teleportZ", teleportPos.getZ());
        }
        if (destinationKey != null) {
            tag.putString("destinationKey", destinationKey.toString());
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("teleportX")) {
            teleportPos = new BlockPos(
                    tag.getInt("teleportX"),
                    tag.getInt("teleportY"),
                    tag.getInt("teleportZ")
            );
        }
        if (tag.contains("destinationKey")) {
            destinationKey = ResourceLocation.tryParse(tag.getString("destinationKey"));
        }
    }
}
