package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.block.BlockInversionPillarEnd;
import com.breakinblocks.neovitae.common.block.type.PillarCapType;
import com.breakinblocks.neovitae.common.blockentity.InversionPillarBlockEntity;
import com.breakinblocks.neovitae.common.dataattachment.NVDataAttachments;
import com.breakinblocks.neovitae.common.dataattachment.DungeonExitData;
import com.breakinblocks.neovitae.common.dimension.DungeonDimensionHelper;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.util.helper.SoulNetworkHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Abstract base class for dungeon rituals containing shared functionality.
 * Provides common methods for portal pillar spawning, rotation, and exit location storage.
 */
public abstract class DungeonRitualBase extends Ritual {

    protected DungeonRitualBase(String name, int crystalLevel, int activationCost, String translationKey) {
        super(name, crystalLevel, activationCost, translationKey);
    }

    @Override
    public boolean activateRitual(IMasterRitualStone masterRitualStone, Player player, UUID owner) {
        storePlayerExitLocation(player);
        return true;
    }

    protected void storePlayerExitLocation(Player player) {
        DungeonExitData exitData = DungeonExitData.of(player.level(), player.blockPosition());
        player.setData(NVDataAttachments.DUNGEON_EXIT.get(), exitData);
    }

    protected void performRitualCleanup(IMasterRitualStone masterRitualStone, Level world) {
        BlockPos masterPos = masterRitualStone.getMasterBlockPos();
        Direction direction = masterRitualStone.getDirection();

        // Replace ritual runes with smooth stone
        List<RitualComponent> components = new ArrayList<>();
        gatherComponents(components::add);

        for (RitualComponent component : components) {
            BlockPos rotatedOffset = rotateOffset(component.offset(), direction);
            BlockPos newPos = masterPos.offset(rotatedOffset);
            world.setBlockAndUpdate(newPos, Blocks.SMOOTH_STONE.defaultBlockState());
        }

        spawnLightningEffect(world, masterPos);
        SoulNetworkHelper.incrementDungeonCounter();
        world.setBlockAndUpdate(masterPos, Blocks.AIR.defaultBlockState());
    }

    protected void spawnLightningEffect(Level world, BlockPos pos) {
        LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(world);
        if (lightning != null) {
            lightning.setPos(pos.getX(), pos.getY() + 1, pos.getZ());
            lightning.setVisualOnly(true);
            world.addFreshEntity(lightning);
        }
    }

    protected void spawnPortalPillar(Level spawnWorld, Level destinationWorld,
                                      BlockPos pillarPos, BlockPos safePlayerPos) {
        spawnWorld.setBlockAndUpdate(pillarPos, NVBlocks.INVERSION_PILLAR.block().get().defaultBlockState());

        BlockEntity tile = spawnWorld.getBlockEntity(pillarPos);
        if (tile instanceof InversionPillarBlockEntity tileInversion) {
            tileInversion.setDestination(destinationWorld, safePlayerPos);

            spawnWorld.setBlockAndUpdate(pillarPos.below(),
                    NVBlocks.INVERSION_PILLAR_CAP.block().get().defaultBlockState()
                            .setValue(BlockInversionPillarEnd.TYPE, PillarCapType.BOTTOM));
            spawnWorld.setBlockAndUpdate(pillarPos.above(),
                    NVBlocks.INVERSION_PILLAR_CAP.block().get().defaultBlockState()
                            .setValue(BlockInversionPillarEnd.TYPE, PillarCapType.TOP));
        }
    }

    protected BlockPos rotateOffset(BlockPos offset, Direction direction) {
        return switch (direction) {
            case NORTH -> offset;
            case EAST -> new BlockPos(-offset.getZ(), offset.getY(), offset.getX());
            case SOUTH -> new BlockPos(-offset.getX(), offset.getY(), -offset.getZ());
            case WEST -> new BlockPos(offset.getZ(), offset.getY(), -offset.getX());
            default -> offset;
        };
    }

    protected ServerLevel getDungeonWorld(Level world) {
        return DungeonDimensionHelper.getDungeonWorld(world);
    }

    @Override
    public int getRefreshTime() {
        return 1; // Execute once immediately
    }

    @Override
    public int getRefreshCost() {
        return 0; // One-time activation cost only
    }
}
