// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2014-2026 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.block.BlockInversionPillarEnd;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonBlocks;
import com.breakinblocks.neovitae.common.block.type.PillarCapType;
import com.breakinblocks.neovitae.common.blockentity.InversionPillarBlockEntity;
import com.breakinblocks.neovitae.common.dataattachment.NVDataAttachments;
import com.breakinblocks.neovitae.common.dataattachment.DungeonExitData;
import com.breakinblocks.neovitae.common.dimension.DungeonDimensionHelper;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.util.helper.AnimaHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.block.state.BlockState;

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
        if (masterRitualStone.getWorldObj() instanceof ServerLevel level) {
            BoundingBox box = getStructureBounds(masterRitualStone, level);
            if (box != null && !hasEnoughSpace(level, box)) {
                player.sendOverlayMessage(Component.translatable(
                        "ritual.neovitae.dungeon.no_space",
                        box.getXSpan(), box.getYSpan(), box.getZSpan()));
                return false;
            }
        }
        storePlayerExitLocation(player);
        return true;
    }

    @Nullable
    protected BoundingBox getStructureBounds(IMasterRitualStone masterRitualStone, ServerLevel level) {
        Optional<StructureTemplate> templateOpt = level.getStructureManager().get(getStructureId());
        if (templateOpt.isEmpty()) {
            return null;
        }
        StructurePlaceSettings settings = new StructurePlaceSettings()
                .setRotation(directionToRotation(masterRitualStone.getDirection()))
                .setMirror(Mirror.NONE)
                .setIgnoreEntities(true)
                .setRotationPivot(ALTERNATOR_LOCAL);
        BlockPos placeOrigin = masterRitualStone.getMasterBlockPos().subtract(ALTERNATOR_LOCAL);
        return templateOpt.get().getBoundingBox(settings, placeOrigin);
    }

    protected boolean hasEnoughSpace(ServerLevel level, BoundingBox box) {
        for (BlockPos pos : BlockPos.betweenClosed(box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ())) {
            BlockState state = level.getBlockState(pos);
            if (state.isAir() || state.canBeReplaced()) {
                continue;
            }
            Identifier id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
            if (id.getNamespace().equals(NeoVitae.MODID)) {
                continue;
            }
            return false;
        }
        return true;
    }

    protected void storePlayerExitLocation(Player player) {
        DungeonExitData exitData = DungeonExitData.of(player.level(), player.blockPosition());
        player.setData(NVDataAttachments.DUNGEON_EXIT.get(), exitData);
    }

    protected void storeControllerPosition(IMasterRitualStone masterRitualStone, BlockPos controllerPos) {
        Level world = masterRitualStone.getWorldObj();
        Player player = world.getPlayerByUUID(masterRitualStone.getOwner());
        if (player != null) {
            DungeonExitData exitData = player.getData(NVDataAttachments.DUNGEON_EXIT.get());
            player.setData(NVDataAttachments.DUNGEON_EXIT.get(), exitData.withControllerPos(controllerPos));
        }
    }

    protected static final BlockPos ALTERNATOR_LOCAL = new BlockPos(4, 0, 4);

    protected abstract Identifier getStructureId();

    protected boolean applyRitualStructure(IMasterRitualStone masterRitualStone, ServerLevel level) {
        BlockPos masterPos = masterRitualStone.getMasterBlockPos();
        Rotation rotation = directionToRotation(masterRitualStone.getDirection());

        Optional<StructureTemplate> templateOpt = level.getStructureManager().get(getStructureId());
        if (templateOpt.isEmpty()) {
            return false;
        }

        StructurePlaceSettings settings = new StructurePlaceSettings()
                .setRotation(rotation)
                .setMirror(Mirror.NONE)
                .setIgnoreEntities(true)
                .setRotationPivot(ALTERNATOR_LOCAL);

        BlockPos placeOrigin = masterPos.subtract(ALTERNATOR_LOCAL);
        templateOpt.get().placeInWorld(level, placeOrigin, ALTERNATOR_LOCAL, settings, level.getRandom(),
                Block.UPDATE_CLIENTS);

        spawnLightningEffect(level, masterPos);
        AnimaHelper.incrementDungeonCounter();
        return true;
    }

    protected void wireFunctionalInversionPillar(ServerLevel spawnWorld, BlockPos masterPos,
                                                  Level destinationWorld, BlockPos safePlayerPos) {
        BlockEntity tile = spawnWorld.getBlockEntity(masterPos.above(2));
        if (tile instanceof InversionPillarBlockEntity tileInversion) {
            tileInversion.setDestination(destinationWorld, safePlayerPos);
        }
    }

    private static Rotation directionToRotation(Direction direction) {
        return switch (direction) {
            case EAST -> Rotation.CLOCKWISE_90;
            case SOUTH -> Rotation.CLOCKWISE_180;
            case WEST -> Rotation.COUNTERCLOCKWISE_90;
            default -> Rotation.NONE;
        };
    }

    protected void spawnLightningEffect(Level world, BlockPos pos) {
        LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(world, EntitySpawnReason.TRIGGERED);
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
            spawnWorld.setBlockAndUpdate(pillarPos.below(2),
                    NVBlocks.BLOODSTONE.block().get().defaultBlockState());
        }

        RandomSource rand = spawnWorld.getRandom();
        int lightCount = 4 + rand.nextInt(6);
        BlockState lightState = NVBlocks.BLOOD_LIGHT.get().defaultBlockState();
        for (int i = 0; i < lightCount; i++) {
            for (int attempt = 0; attempt < 10; attempt++) {
                int dx = rand.nextInt(9) - 4;
                int dy = rand.nextInt(5) - 1;
                int dz = rand.nextInt(9) - 4;
                BlockPos lightPos = pillarPos.offset(dx, dy, dz);
                if (spawnWorld.isEmptyBlock(lightPos)) {
                    spawnWorld.setBlockAndUpdate(lightPos, lightState);
                    break;
                }
            }
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
