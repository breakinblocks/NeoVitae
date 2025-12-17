package com.breakinblocks.neovitae.structures;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nullable;

/**
 * Structure processor that replaces dungeon stone with ore blocks
 * based on a probability (integrity).
 */
public class StoneToOreProcessor extends StructureProcessor {

    public static final MapCodec<StoneToOreProcessor> CODEC = MapCodec.unit(new StoneToOreProcessor(0.0f));

    private final float integrity;

    public StoneToOreProcessor(float integrity) {
        this.integrity = integrity;
    }

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo process(LevelReader levelReader, BlockPos blockPos,
                                                        BlockPos blockPos2,
                                                        StructureTemplate.StructureBlockInfo blockInfoBefore,
                                                        StructureTemplate.StructureBlockInfo blockInfoAfter,
                                                        StructurePlaceSettings settings,
                                                        @Nullable StructureTemplate template) {
        // Dungeon decorative blocks (dungeon_stone, dungeon_ore) from 1.20.1 not yet ported
        // Using stone -> iron ore as placeholder until dungeon blocks are added
        if (blockInfoAfter.state().is(Blocks.STONE)) {
            RandomSource random = settings.getRandom(blockInfoAfter.pos());
            if (this.integrity < 1.0F && random.nextFloat() < this.integrity) {
                return new StructureTemplate.StructureBlockInfo(
                        blockInfoAfter.pos(),
                        Blocks.IRON_ORE.defaultBlockState(),
                        blockInfoAfter.nbt()
                );
            }
        }
        return blockInfoAfter;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return StructureProcessorType.BLOCK_ROT;
    }
}
