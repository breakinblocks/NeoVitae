package com.breakinblocks.neovitae.structures;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import com.breakinblocks.neovitae.common.tag.NVTags;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class IronOreToGenerativeProcessor extends StructureProcessor {

    public static final MapCodec<IronOreToGenerativeProcessor> CODEC = MapCodec.unit(new IronOreToGenerativeProcessor());

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo process(LevelReader levelReader, BlockPos blockPos,
                                                        BlockPos blockPos2,
                                                        StructureTemplate.StructureBlockInfo blockInfoBefore,
                                                        StructureTemplate.StructureBlockInfo blockInfoAfter,
                                                        StructurePlaceSettings settings,
                                                        @Nullable StructureTemplate template) {
        BlockState state = blockInfoAfter.state();
        if (state.is(Blocks.IRON_ORE) || state.is(Blocks.DEEPSLATE_IRON_ORE)) {
            Block replacement = pickGenerativeOre(settings.getRandom(blockInfoAfter.pos()));
            if (replacement != null) {
                return new StructureTemplate.StructureBlockInfo(
                        blockInfoAfter.pos(),
                        replacement.defaultBlockState(),
                        blockInfoAfter.nbt());
            }
        }
        return blockInfoAfter;
    }

    @Nullable
    private static Block pickGenerativeOre(RandomSource random) {
        List<Block> candidates = new ArrayList<>();
        BuiltInRegistries.BLOCK.getTagOrEmpty(NVTags.Blocks.GENERATIVE_ORES).forEach(holder -> {
            Block block = holder.value();
            if (block != Blocks.IRON_ORE && block != Blocks.DEEPSLATE_IRON_ORE) {
                candidates.add(block);
            }
        });
        if (candidates.isEmpty()) return null;
        return candidates.get(random.nextInt(candidates.size()));
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return StructureProcessorType.BLOCK_ROT;
    }
}
