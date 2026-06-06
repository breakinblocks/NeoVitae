// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2020-2023 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.structures;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Wrapper class for placing NBT structure templates in the world.
 */
public class DungeonStructure {
    private static final Logger LOGGER = LoggerFactory.getLogger(DungeonStructure.class);

    public final Identifier resource;

    public DungeonStructure(Identifier resource) {
        this.resource = resource;
    }

    /**
     * Places the structure at the given position using the provided settings.
     *
     * @param rand     Random source for structure placement
     * @param settings Placement settings (rotation, mirror, etc.)
     * @param world    The server level to place in
     * @param pos      The position to place the structure
     * @return true if placement succeeded, false otherwise
     */
    public boolean placeStructureAtPosition(RandomSource rand, StructurePlaceSettings settings,
                                            ServerLevel world, BlockPos pos) {
        if (pos == null) {
            return false;
        }

        StructureTemplateManager templateManager = world.getStructureManager();
        Optional<StructureTemplate> template = templateManager.get(resource);

        if (template.isEmpty()) {
            LOGGER.warn("Invalid template for location: {}", resource);
            return false;
        }

        BlockPos offset = StructureTemplate.calculateRelativePosition(settings, BlockPos.ZERO);
        BlockPos finalPos = pos.offset(offset);

        BoundingBox bb = template.get().getBoundingBox(settings, finalPos);
        List<ChunkPos> chunks = new ArrayList<>();
        for (int x = SectionPos.blockToSectionCoord(bb.minX()); x <= SectionPos.blockToSectionCoord(bb.maxX()); x++) {
            for (int z = SectionPos.blockToSectionCoord(bb.minZ()); z <= SectionPos.blockToSectionCoord(bb.maxZ()); z++) {
                chunks.add(new ChunkPos(x, z));
                world.setChunkForced(x, z, true);
            }
        }

        try {
            template.get().placeInWorld(world, finalPos, finalPos, settings, rand, 2);
        } catch (Exception e) {
            LOGGER.error("Exception placing structure {} at {}: {}", resource, finalPos, e.getMessage(), e);
            return false;
        } finally {
            for (ChunkPos chunk : chunks) {
                world.setChunkForced(chunk.x(), chunk.z(), false);
            }
        }

        return true;
    }

    /**
     * Creates a copy of this DungeonStructure.
     */
    public DungeonStructure copy() {
        return new DungeonStructure(resource);
    }

    @Override
    public String toString() {
        return "DungeonStructure{" + resource + "}";
    }
}
