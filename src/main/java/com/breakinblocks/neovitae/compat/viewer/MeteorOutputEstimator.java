package com.breakinblocks.neovitae.compat.viewer;

import com.breakinblocks.neovitae.common.meteor.MeteorLayer;
import com.breakinblocks.neovitae.common.meteor.RandomBlockContainer;
import com.breakinblocks.neovitae.common.meteor.RandomBlockTagContainer;
import com.breakinblocks.neovitae.common.recipe.meteor.MeteorRecipe;
import org.apache.commons.lang3.tuple.Pair;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Shared meteor output estimation for recipe viewers. Both the JEI and EMI
 * plugins render the same weighted block breakdown, so the volume maths lives
 * here rather than being mirrored in two places.
 */
public final class MeteorOutputEstimator {

    public static final int MAX_SLOTS = 24;

    public record Estimate(List<ItemStack> stacks, int count, double percentage) {
        public int poolSize() {
            return stacks.size();
        }
    }

    private MeteorOutputEstimator() {
    }

    public static int maxRadius(MeteorRecipe recipe) {
        int max = 0;
        for (MeteorLayer layer : recipe.getLayerList()) {
            max = Math.max(max, layer.getLayerRadius());
        }
        return max;
    }

    public static List<Estimate> estimate(MeteorRecipe recipe) {
        List<MeteorLayer> layers = recipe.getLayerList();
        int totalEstimatedBlocks = 0;
        Map<List<Block>, Double> blockEstimates = new LinkedHashMap<>();

        for (int i = 0; i < layers.size(); i++) {
            MeteorLayer layer = layers.get(i);
            int radius = layer.getLayerRadius();
            int innerRadius = (i > 0) ? layers.get(i - 1).getLayerRadius() : -1;
            int layerVolume = sphereVolume(radius) - (innerRadius >= 0 ? sphereVolume(innerRadius) : 0);
            totalEstimatedBlocks += layerVolume;

            int weightedTotal = 0;
            for (Pair<RandomBlockContainer, Integer> entry : layer.getWeightList()) {
                weightedTotal += entry.getValue();
            }
            int totalWeight = Math.max(layer.getMinWeight(), layer.getAdditionalTotalWeight() + weightedTotal);

            List<Block> fillBlocks = blocksFrom(layer.getFillBlock());
            if (!fillBlocks.isEmpty()) {
                double fillEstimate = totalWeight > 0
                        ? layerVolume * ((double) (totalWeight - weightedTotal) / totalWeight)
                        : layerVolume;
                blockEstimates.merge(fillBlocks, fillEstimate, Double::sum);
            }

            if (totalWeight > 0) {
                for (Pair<RandomBlockContainer, Integer> entry : layer.getWeightList()) {
                    List<Block> blocks = blocksFrom(entry.getKey());
                    if (blocks.isEmpty()) continue;
                    blockEstimates.merge(blocks, (double) entry.getValue() / totalWeight * layerVolume, Double::sum);
                }
            }

            if (layer.getShellBlock() != null) {
                List<Block> shellBlocks = blocksFrom(layer.getShellBlock());
                if (!shellBlocks.isEmpty()) {
                    int shellEstimate = sphereVolume(radius) - sphereVolume(radius - 1);
                    blockEstimates.merge(shellBlocks, (double) shellEstimate, Double::sum);
                }
            }
        }

        List<Map.Entry<List<Block>, Double>> sorted = new ArrayList<>(blockEstimates.entrySet());
        sorted.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        List<Estimate> out = new ArrayList<>();
        for (Map.Entry<List<Block>, Double> entry : sorted) {
            if (out.size() >= MAX_SLOTS) break;
            List<ItemStack> stacks = new ArrayList<>();
            for (Block block : entry.getKey()) {
                ItemStack stack = new ItemStack(block);
                if (!stack.isEmpty()) stacks.add(stack);
            }
            if (stacks.isEmpty()) continue;
            int count = (int) Math.round(entry.getValue());
            double percentage = totalEstimatedBlocks > 0 ? (double) count / totalEstimatedBlocks * 100 : 0;
            out.add(new Estimate(stacks, count, percentage));
        }
        return out;
    }

    private static int sphereVolume(int radius) {
        if (radius < 0) return 0;
        int count = 0;
        float rSquared = (radius + 0.5f) * (radius + 0.5f);
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + y * y + z * z <= rSquared) count++;
                }
            }
        }
        return count;
    }

    private static List<Block> blocksFrom(RandomBlockContainer container) {
        if (container == null) return List.of();

        if (container instanceof RandomBlockTagContainer tagContainer) {
            List<Block> blocks = new ArrayList<>();
            BuiltInRegistries.BLOCK.getTagOrEmpty(tagContainer.getTag()).forEach(holder -> blocks.add(holder.value()));
            int index = tagContainer.getIndex();
            if (index >= 0) {
                if (index >= blocks.size()) return List.of();
                Block block = blocks.get(index);
                return block == Blocks.AIR ? List.of() : List.of(block);
            }
            blocks.removeIf(block -> block == Blocks.AIR);
            return blocks;
        }

        Block block = singleBlock(container);
        return block == null || block == Blocks.AIR ? List.of() : List.of(block);
    }

    @Nullable
    private static Block singleBlock(RandomBlockContainer container) {
        if (container == null) return null;
        String entry = container.getEntry();

        if (entry.startsWith("#")) {
            String tagName = entry.substring(1);
            if (tagName.contains("#")) {
                tagName = tagName.substring(0, tagName.indexOf("#"));
            }
            try {
                TagKey<Block> tagKey = TagKey.create(Registries.BLOCK, Identifier.parse(tagName));
                for (Holder<Block> holder : BuiltInRegistries.BLOCK.getTagOrEmpty(tagKey)) {
                    return holder.value();
                }
            } catch (Exception e) {
                return null;
            }
            return null;
        }

        if (entry.startsWith(";")) {
            try {
                var fluid = BuiltInRegistries.FLUID.get(Identifier.parse(entry.substring(1)));
                if (fluid.isPresent()) return fluid.get().value().defaultFluidState().createLegacyBlock().getBlock();
            } catch (Exception e) {
                return null;
            }
            return null;
        }

        try {
            return BuiltInRegistries.BLOCK.get(Identifier.parse(entry)).map(Holder.Reference::value).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }
}
