package com.breakinblocks.neovitae.datagen;

import net.minecraft.world.level.block.Block;
import com.breakinblocks.neovitae.common.block.NVBlocks;

import java.util.List;

public class BlockGroups {
    public static List<Block> RUNE_T1 = List.of(
            NVBlocks.RUNE_BLANK.block().get(), NVBlocks.RUNE_EFFICIENCY.block().get(),
            NVBlocks.RUNE_SACRIFICE.block().get(), NVBlocks.RUNE_SELF_SACRIFICE.block().get(),
            NVBlocks.RUNE_SPEED.block().get(), NVBlocks.RUNE_ACCELERATION.block().get(), NVBlocks.RUNE_DISLOCATION.block().get(),
            NVBlocks.RUNE_CAPACITY.block().get(), NVBlocks.RUNE_CAPACITY_AUGMENTED.block().get(),
            NVBlocks.RUNE_ORB.block().get(), NVBlocks.RUNE_CHARGING.block().get()
    );

    public static List<Block> RUNE_T2 = List.of(
            NVBlocks.RUNE_2_EFFICIENCY.block().get(),
            NVBlocks.RUNE_2_SACRIFICE.block().get(), NVBlocks.RUNE_2_SELF_SACRIFICE.block().get(),
            NVBlocks.RUNE_2_SPEED.block().get(), NVBlocks.RUNE_2_ACCELERATION.block().get(), NVBlocks.RUNE_2_DISLOCATION.block().get(),
            NVBlocks.RUNE_2_CAPACITY.block().get(), NVBlocks.RUNE_2_CAPACITY_AUGMENTED.block().get(),
            NVBlocks.RUNE_2_ORB.block().get(), NVBlocks.RUNE_2_CHARGING.block().get()
    );

    public static List<Block> BLOODSTONE = List.of(
            NVBlocks.BLOODSTONE.block().get(), NVBlocks.BLOODSTONE_BRICK.block().get()
    );

    public static List<Block> HELLFORGED_BLOCK = List.of( // theres textures for the other types for it
            NVBlocks.HELLFORGED_BLOCK.block().get()
    );

    public static List<Block> CRYSTAL_CLUSTER = List.of(
            NVBlocks.CRYSTAL_CLUSTER.block().get(), NVBlocks.CRYSTAL_CLUSTER_BRICK.block().get()
    );
}
