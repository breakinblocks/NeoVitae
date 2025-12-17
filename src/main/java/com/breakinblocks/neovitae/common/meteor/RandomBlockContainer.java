package com.breakinblocks.neovitae.common.meteor;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import java.util.Optional;

/**
 * Base class for random block generation in meteor layers.
 * Supports static blocks, fluids, and block tags with weighted selection.
 */
public abstract class RandomBlockContainer {

    public abstract Block getRandomBlock(RandomSource rand, Level level);

    public abstract String getEntry();

    /**
     * Parses an entry string into a RandomBlockContainer.
     * Format:
     * - "#namespace:path" for block tags
     * - "#namespace:path#index" for block tags with static index
     * - ";namespace:path" for fluids
     * - "namespace:path" for static blocks
     */
    public static RandomBlockContainer parseEntry(String str) {
        if (str.startsWith("#")) {
            String[] splitStr = str.split("#");
            int index = -1;
            String tagName = splitStr[1];

            TagKey<Block> tag = TagKey.create(Registries.BLOCK, ResourceLocation.parse(tagName));

            if (splitStr.length > 2) {
                try {
                    index = Integer.parseInt(splitStr[2]);
                } catch (NumberFormatException ignored) {
                }
            }

            return new RandomBlockTagContainer(tag, index);
        } else if (str.startsWith(";")) {
            String[] splitStr = str.split(";");
            String fluidName = splitStr[1];
            return parseFluidEntry(fluidName);
        } else {
            return parseBlockEntry(str);
        }
    }

    public static RandomBlockContainer parseTagEntry(String str, int index) {
        TagKey<Block> tag = TagKey.create(Registries.BLOCK, ResourceLocation.parse(str));
        return new RandomBlockTagContainer(tag, index);
    }

    public static RandomBlockContainer parseBlockEntry(String str) {
        Block block = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(str));
        if (block == null) {
            return null;
        }
        return new StaticBlockContainer(block);
    }

    public static RandomBlockContainer parseFluidEntry(String str) {
        Optional<Fluid> fluid = BuiltInRegistries.FLUID.getOptional(ResourceLocation.parse(str));
        return fluid.map(FluidBlockContainer::new).orElse(null);
    }
}
