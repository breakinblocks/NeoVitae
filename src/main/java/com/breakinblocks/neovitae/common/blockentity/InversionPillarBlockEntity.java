package com.breakinblocks.neovitae.common.blockentity;


import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.Set;

public class InversionPillarBlockEntity extends BaseBlockEntity {

    private static final Logger LOGGER = LoggerFactory.getLogger(InversionPillarBlockEntity.class);

    @Nullable
    private BlockPos teleportPos;
    @Nullable
    private Identifier destinationKey;

    public InversionPillarBlockEntity(BlockPos pos, BlockState state) {
        super(NVTiles.INVERSION_PILLAR_TYPE.get(), pos, state);
    }

    public void setDestination(BlockPos pos, Identifier dimension) {
        this.teleportPos = pos;
        this.destinationKey = dimension;
        setChanged();
        LOGGER.info("Pillar at {} set destination to {} in {}", worldPosition, pos, dimension);
    }

    public void setDestination(Level destinationWorld, BlockPos destinationPos) {
        setDestination(destinationPos, destinationWorld.dimension().identifier());
    }

    public boolean hasDestination() {
        return teleportPos != null && destinationKey != null;
    }

    @Nullable
    public BlockPos getTeleportPos() {
        return teleportPos;
    }

    @Nullable
    public Identifier getDestinationKey() {
        return destinationKey;
    }

    public void handlePlayerInteraction(Player player) {
        if (level == null || level.isClientSide()) return;

        if (!hasDestination()) {
            return;
        }

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        ServerLevel serverLevel = (ServerLevel) level;

        ResourceKey<Level> destKey = ResourceKey.create(Registries.DIMENSION, destinationKey);
        ServerLevel destLevel = serverLevel.getServer().getLevel(destKey);

        if (destLevel == null) {
            LOGGER.warn("Could not find destination dimension: {}", destinationKey);
            return;
        }

        if (com.breakinblocks.neovitae.common.dimension.DungeonDimensionHelper.isDungeonDimension(destLevel)) {
            com.breakinblocks.neovitae.common.event.CommonEventHandler.setDungeonGracePeriod(player, 100);
        }

        serverPlayer.teleportTo(
                destLevel,
                teleportPos.getX() + 0.5,
                teleportPos.getY(),
                teleportPos.getZ() + 0.5,
                Set.of(),
                player.getYRot(),
                player.getXRot(),
                true
        );
    }

    @Override
    protected void saveAdditional(ValueOutput tag) {
        super.saveAdditional(tag);
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
    protected void loadAdditional(ValueInput tag) {
        super.loadAdditional(tag);
        int tx = tag.getIntOr("teleportX", Integer.MIN_VALUE);
        if (tx != Integer.MIN_VALUE) {
            teleportPos = new BlockPos(tx,
                    tag.getIntOr("teleportY", 0),
                    tag.getIntOr("teleportZ", 0));
        }
        String dk = tag.getStringOr("destinationKey", "");
        if (!dk.isEmpty()) {
            destinationKey = Identifier.tryParse(dk);
        }
    }
}
