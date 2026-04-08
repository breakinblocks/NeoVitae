package com.breakinblocks.neovitae.structures;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nullable;
import java.util.Map;

public class TrialSpawnerEntityProcessor extends StructureProcessor {

    public static final MapCodec<TrialSpawnerEntityProcessor> CODEC = MapCodec.unit(TrialSpawnerEntityProcessor::new);

    private static final Map<String, String> REPLACEMENTS = Map.of(
            "neovitae:daemonium_doloris", "neovitae:slime_vitae"
    );

    private static final Map<String, Integer> SPAWN_COUNT_OVERRIDES = Map.of(
            "neovitae:slime_vitae", 3
    );

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo process(LevelReader levelReader, BlockPos blockPos,
                                                        BlockPos blockPos2,
                                                        StructureTemplate.StructureBlockInfo blockInfoBefore,
                                                        StructureTemplate.StructureBlockInfo blockInfoAfter,
                                                        StructurePlaceSettings settings,
                                                        @Nullable StructureTemplate template) {
        if (!blockInfoAfter.state().is(Blocks.TRIAL_SPAWNER) || blockInfoAfter.nbt() == null) {
            return blockInfoAfter;
        }

        CompoundTag nbt = blockInfoAfter.nbt().copy();
        boolean modified = false;

        modified |= replaceInSpawnData(nbt);
        modified |= replaceInConfig(nbt, "normal_config");
        modified |= replaceInConfig(nbt, "ominous_config");

        if (modified) {
            return new StructureTemplate.StructureBlockInfo(blockInfoAfter.pos(), blockInfoAfter.state(), nbt);
        }
        return blockInfoAfter;
    }

    private boolean replaceInSpawnData(CompoundTag root) {
        if (!root.contains("spawn_data")) return false;
        CompoundTag spawnData = root.getCompound("spawn_data");
        if (!spawnData.contains("entity")) return false;
        CompoundTag entity = spawnData.getCompound("entity");
        return replaceEntityId(entity);
    }

    private boolean replaceInConfig(CompoundTag root, String configKey) {
        if (!root.contains(configKey)) return false;
        CompoundTag config = root.getCompound(configKey);
        boolean modified = false;

        if (config.contains("spawn_potentials")) {
            ListTag potentials = config.getList("spawn_potentials", 10);
            for (int i = 0; i < potentials.size(); i++) {
                CompoundTag potential = potentials.getCompound(i);
                if (potential.contains("data")) {
                    CompoundTag data = potential.getCompound("data");
                    if (data.contains("entity")) {
                        modified |= replaceEntityId(data.getCompound("entity"));
                    }
                }
            }
        }

        if (modified) {
            applySpawnCountOverrides(config);
        }

        return modified;
    }

    private boolean replaceEntityId(CompoundTag entityTag) {
        if (!entityTag.contains("id")) return false;
        String id = entityTag.getString("id");
        String replacement = REPLACEMENTS.get(id);
        if (replacement != null) {
            entityTag.putString("id", replacement);
            return true;
        }
        return false;
    }

    private void applySpawnCountOverrides(CompoundTag config) {
        config.putInt("simultaneous_mobs", 3);
        config.putInt("total_mobs", 9);
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return StructureProcessorType.BLOCK_ROT;
    }
}
