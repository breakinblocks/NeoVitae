package com.breakinblocks.neovitae.common.datamap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

/**
 * Data-driven imperfect ritual statistics that can be customized via datapacks.
 *
 * @param activationCost LP cost to activate the ritual
 * @param block The block required above the ritual stone (as registry name)
 * @param blockTag Optional block tag that can be used instead of a specific block
 * @param consumeBlock Whether the catalyst block should be consumed on activation
 * @param lightningEffect Whether to spawn lightning on activation
 * @param enabled Whether the ritual is enabled (disabled rituals cannot be activated)
 */
public record ImperfectRitualStats(
        int activationCost,
        Optional<Block> block,
        Optional<TagKey<Block>> blockTag,
        boolean consumeBlock,
        boolean lightningEffect,
        boolean enabled
) {
    public static final Codec<ImperfectRitualStats> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("activation_cost").forGetter(ImperfectRitualStats::activationCost),
            BuiltInRegistries.BLOCK.byNameCodec().optionalFieldOf("block").forGetter(ImperfectRitualStats::block),
            TagKey.codec(BuiltInRegistries.BLOCK.key()).optionalFieldOf("block_tag").forGetter(ImperfectRitualStats::blockTag),
            Codec.BOOL.optionalFieldOf("consume_block", false).forGetter(ImperfectRitualStats::consumeBlock),
            Codec.BOOL.optionalFieldOf("lightning_effect", true).forGetter(ImperfectRitualStats::lightningEffect),
            Codec.BOOL.optionalFieldOf("enabled", true).forGetter(ImperfectRitualStats::enabled)
    ).apply(instance, ImperfectRitualStats::new));

    /**
     * Creates stats with a specific block requirement.
     */
    public static ImperfectRitualStats ofBlock(int activationCost, Block block, boolean consumeBlock, boolean lightningEffect) {
        return new ImperfectRitualStats(activationCost, Optional.of(block), Optional.empty(), consumeBlock, lightningEffect, true);
    }

    /**
     * Creates stats with a block tag requirement.
     */
    public static ImperfectRitualStats ofTag(int activationCost, TagKey<Block> blockTag, boolean consumeBlock, boolean lightningEffect) {
        return new ImperfectRitualStats(activationCost, Optional.empty(), Optional.of(blockTag), consumeBlock, lightningEffect, true);
    }

    /**
     * Simple helper - block requirement, no consumption, with lightning.
     */
    public static ImperfectRitualStats simple(int activationCost, Block block) {
        return ofBlock(activationCost, block, false, true);
    }

    /**
     * Simple helper - block requirement, no consumption, no lightning.
     */
    public static ImperfectRitualStats simpleQuiet(int activationCost, Block block) {
        return ofBlock(activationCost, block, false, false);
    }
}
