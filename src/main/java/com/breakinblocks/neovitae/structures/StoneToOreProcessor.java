// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2020-2023 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.structures;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonBlocks;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonVariant;

import javax.annotation.Nullable;

public class StoneToOreProcessor extends StructureProcessor {

    public static final MapCodec<StoneToOreProcessor> CODEC = MapCodec.unit(new StoneToOreProcessor(0.0f));

    private final float integrity;

    public StoneToOreProcessor(float integrity) {
        this.integrity = integrity;
    }

    private static final float PRISMATIC_CHANCE = 0.2f;

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo process(LevelReader levelReader, BlockPos blockPos,
                                                        BlockPos blockPos2,
                                                        StructureTemplate.StructureBlockInfo blockInfoBefore,
                                                        StructureTemplate.StructureBlockInfo blockInfoAfter,
                                                        StructurePlaceSettings settings,
                                                        @Nullable StructureTemplate template) {
        if (blockInfoAfter.state().is(DungeonBlocks.DUNGEON_STONE.get(DungeonVariant.RAW).block().get())) {
            RandomSource random = settings.getRandom(blockInfoAfter.pos());
            if (this.integrity > 0 && random.nextFloat() < this.integrity) {
                if (random.nextFloat() < PRISMATIC_CHANCE) {
                    return new StructureTemplate.StructureBlockInfo(
                            blockInfoAfter.pos(),
                            DungeonBlocks.PRISMATIC_DEMONITE.block().get().defaultBlockState(),
                            blockInfoAfter.nbt());
                }
                return new StructureTemplate.StructureBlockInfo(
                        blockInfoAfter.pos(),
                        DungeonBlocks.DUNGEON_ORE.block().get().defaultBlockState(),
                        blockInfoAfter.nbt());
            }
        }
        return blockInfoAfter;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return StructureProcessorType.BLOCK_ROT;
    }

}
