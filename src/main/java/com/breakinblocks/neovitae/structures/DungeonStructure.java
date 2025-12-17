package com.breakinblocks.neovitae.structures;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Wrapper class for placing NBT structure templates in the world.
 */
public class DungeonStructure {
    private static final Logger LOGGER = LoggerFactory.getLogger(DungeonStructure.class);

    public final ResourceLocation resource;

    public DungeonStructure(ResourceLocation resource) {
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

        template.get().placeInWorld(world, finalPos, finalPos, settings, rand, 2);
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
