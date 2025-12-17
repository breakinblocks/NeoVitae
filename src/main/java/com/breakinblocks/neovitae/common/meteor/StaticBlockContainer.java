package com.breakinblocks.neovitae.common.meteor;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

/**
 * A RandomBlockContainer that always returns the same static block.
 */
public class StaticBlockContainer extends RandomBlockContainer {

    private final Block block;

    public StaticBlockContainer(Block block) {
        this.block = block;
    }

    @Override
    public Block getRandomBlock(RandomSource rand, Level level) {
        return block;
    }

    @Override
    public String getEntry() {
        return BuiltInRegistries.BLOCK.getKey(block).toString();
    }

    public Block getBlock() {
        return block;
    }
}
