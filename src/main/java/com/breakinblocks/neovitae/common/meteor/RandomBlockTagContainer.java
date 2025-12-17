package com.breakinblocks.neovitae.common.meteor;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A RandomBlockContainer that selects from a block tag.
 * Can optionally use a fixed index for deterministic selection.
 */
public class RandomBlockTagContainer extends RandomBlockContainer {

    private final TagKey<Block> tag;
    private final int index;

    public RandomBlockTagContainer(TagKey<Block> tag, int index) {
        this.tag = tag;
        this.index = index;
    }

    @Override
    public Block getRandomBlock(RandomSource rand, Level level) {
        List<Block> list = new ArrayList<>();
        BuiltInRegistries.BLOCK.getTagOrEmpty(tag).forEach(holder -> list.add(holder.value()));

        if (list.isEmpty()) {
            return null;
        }

        if (index >= 0 && index < list.size()) {
            return list.get(index);
        }

        return list.get(rand.nextInt(list.size()));
    }

    @Override
    public String getEntry() {
        ResourceLocation rl = tag.location();
        String entry = "#" + rl.toString();
        if (index >= 0) {
            entry = entry + "#" + index;
        }
        return entry;
    }

    public TagKey<Block> getTag() {
        return tag;
    }

    public int getIndex() {
        return index;
    }
}
