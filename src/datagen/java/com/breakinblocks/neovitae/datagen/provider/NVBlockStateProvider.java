package com.breakinblocks.neovitae.datagen.provider;

import com.breakinblocks.neovitae.NeoVitae;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.stream.Stream;

/**
 * TODO(phase15 stage2): Full rewrite against net.minecraft.client.data.models.ModelProvider.
 * Stubbed for Stage 1 - generated block JSON is already committed in src/generated/resources.
 */
public class NVBlockStateProvider extends ModelProvider {

    public NVBlockStateProvider(PackOutput output) {
        super(output, NeoVitae.MODID);
    }

    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks() {
        return Stream.empty();
    }

    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
        return Stream.empty();
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        // Intentionally empty - see class-level TODO.
    }
}
