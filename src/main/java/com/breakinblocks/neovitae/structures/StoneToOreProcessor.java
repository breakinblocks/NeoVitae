package com.breakinblocks.neovitae.structures;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
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
import com.breakinblocks.neovitae.common.datamap.NVDataMaps;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class StoneToOreProcessor extends StructureProcessor {

    public static final MapCodec<StoneToOreProcessor> CODEC = MapCodec.unit(new StoneToOreProcessor(0.0f));

    private final float integrity;
    private List<WeightedOre> cachedOres;
    private int cachedTotalWeight;

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
        if (blockInfoAfter.state().is(Blocks.STONE)) {
            RandomSource random = settings.getRandom(blockInfoAfter.pos());
            if (this.integrity > 0 && random.nextFloat() < this.integrity) {
                BlockState ore = getRandomOre(random);
                if (ore != null) {
                    return new StructureTemplate.StructureBlockInfo(
                            blockInfoAfter.pos(), ore, blockInfoAfter.nbt());
                }
            }
        }
        return blockInfoAfter;
    }

    @Nullable
    private BlockState getRandomOre(RandomSource random) {
        if (cachedOres == null) {
            cachedOres = new ArrayList<>();
            cachedTotalWeight = 0;
            for (Holder<Block> holder : BuiltInRegistries.BLOCK.holders().toList()) {
                Integer weight = holder.getData(NVDataMaps.DUNGEON_ORE_WEIGHTS);
                if (weight != null && weight > 0) {
                    cachedOres.add(new WeightedOre(holder.value().defaultBlockState(), weight));
                    cachedTotalWeight += weight;
                }
            }
        }

        if (cachedOres.isEmpty() || cachedTotalWeight <= 0) return null;

        int roll = random.nextInt(cachedTotalWeight);
        for (WeightedOre ore : cachedOres) {
            roll -= ore.weight;
            if (roll < 0) return ore.state;
        }
        return cachedOres.getLast().state;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return StructureProcessorType.BLOCK_ROT;
    }

    private record WeightedOre(BlockState state, int weight) {}
}
