package com.breakinblocks.neovitae.datagen;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import com.breakinblocks.neovitae.common.block.NVBlocks;

import java.util.List;

public class BlockGroups {
    public static List<ResourceKey<Block>> RUNE_T1 = List.of(
            NVBlocks.RUNE_BLANK.block().getKey(), NVBlocks.RUNE_EFFICIENCY.block().getKey(),
            NVBlocks.RUNE_SACRIFICE.block().getKey(), NVBlocks.RUNE_SELF_SACRIFICE.block().getKey(),
            NVBlocks.RUNE_SPEED.block().getKey(), NVBlocks.RUNE_ACCELERATION.block().getKey(), NVBlocks.RUNE_DISLOCATION.block().getKey(),
            NVBlocks.RUNE_CAPACITY.block().getKey(), NVBlocks.RUNE_CAPACITY_AUGMENTED.block().getKey(),
            NVBlocks.RUNE_ORB.block().getKey(), NVBlocks.RUNE_CHARGING.block().getKey()
    );

    public static List<ResourceKey<Block>> RUNE_T2 = List.of(
            NVBlocks.RUNE_2_EFFICIENCY.block().getKey(),
            NVBlocks.RUNE_2_SACRIFICE.block().getKey(), NVBlocks.RUNE_2_SELF_SACRIFICE.block().getKey(),
            NVBlocks.RUNE_2_SPEED.block().getKey(), NVBlocks.RUNE_2_ACCELERATION.block().getKey(), NVBlocks.RUNE_2_DISLOCATION.block().getKey(),
            NVBlocks.RUNE_2_CAPACITY.block().getKey(), NVBlocks.RUNE_2_CAPACITY_AUGMENTED.block().getKey(),
            NVBlocks.RUNE_2_ORB.block().getKey(), NVBlocks.RUNE_2_CHARGING.block().getKey()
    );

    public static List<ResourceKey<Block>> BLOODSTONE = List.of(
            NVBlocks.BLOODSTONE.block().getKey(), NVBlocks.BLOODSTONE_BRICK.block().getKey()
    );

    public static List<ResourceKey<Block>> HELLFORGED_BLOCK = List.of( // theres textures for the other types for it
            NVBlocks.HELLFORGED_BLOCK.block().getKey()
    );

    public static List<ResourceKey<Block>> CRYSTAL_CLUSTER = List.of(
            NVBlocks.CRYSTAL_CLUSTER.block().getKey(), NVBlocks.CRYSTAL_CLUSTER_BRICK.block().getKey()
    );
}
