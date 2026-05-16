package com.breakinblocks.neovitae.util.helper;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import com.breakinblocks.neovitae.client.util.ClientTextureResources;
import com.breakinblocks.neovitae.common.material.MaterialDefinition;
import com.breakinblocks.neovitae.common.material.MaterialRegistry;
import com.mojang.blaze3d.platform.NativeImage;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SmeltingRecipe;

public final class OreDiscoveryHelper {

    private OreDiscoveryHelper() {}

    private static ResourceManager clientResourceManagerIfAvailable(ServerLevel level) {
        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            try {
                return ClientTextureResources.get();
            } catch (Throwable ignored) {}
        }
        return level.getServer().getResourceManager();
    }

    public static List<MaterialDefinition> discoverNewMaterials(ServerLevel level) {
        Registry<Item> itemRegistry = level.registryAccess().lookupOrThrow(Registries.ITEM);
        ResourceManager resourceManager = clientResourceManagerIfAvailable(level);

        List<String> oreNames = TagHelper.getOreNames(itemRegistry);
        List<MaterialDefinition> newMaterials = new ArrayList<>();

        for (String oreName : oreNames) {
            if (MaterialRegistry.hasMaterial(oreName)) continue;

            Optional<Item> firstOre = TagHelper.getFirstItem(itemRegistry, "c", "ores/" + oreName);
            if (firstOre.isEmpty()) continue;

            String smeltTo = findSmeltOutput(level, itemRegistry, firstOre.get(), oreName);
            String rawTag = TagHelper.tagHasItems(itemRegistry, "c", "raw_materials/" + oreName) ? "c:raw_materials/" + oreName : null;

            String ingotTag = null;
            if (TagHelper.tagHasItems(itemRegistry, "c", "ingots/" + oreName)) {
                ingotTag = "c:ingots/" + oreName;
            } else if (TagHelper.tagHasItems(itemRegistry, "c", "gems/" + oreName)) {
                ingotTag = "c:gems/" + oreName;
            }

            String color = extractOreColor(resourceManager, firstOre.get());

            newMaterials.add(new MaterialDefinition(
                    oreName, color, List.of("fragment", "gravel", "dust"),
                    smeltTo, smeltTo != null ? 0.7f : 0,
                    "c:ores/" + oreName, rawTag, ingotTag,
                    null, null
            ));
        }

        return newMaterials;
    }

    @Nullable
    public static String findSmeltOutput(ServerLevel level, Registry<Item> itemRegistry, Item oreItem, String oreName) {
        var input = new SingleRecipeInput(new ItemStack(oreItem));
        Optional<RecipeHolder<SmeltingRecipe>> recipe = level.recipeAccess().getRecipeFor(RecipeType.SMELTING, input, level);
        if (recipe.isPresent()) {
            ItemStack result = recipe.get().value().assemble(input);
            if (!result.isEmpty()) {
                return BuiltInRegistries.ITEM.getKey(result.getItem()).toString();
            }
        }

        Optional<Item> ingot = TagHelper.getFirstItem(itemRegistry, "c", "ingots/" + oreName);
        if (ingot.isPresent()) {
            return BuiltInRegistries.ITEM.getKey(ingot.get()).toString();
        }

        Optional<Item> gem = TagHelper.getFirstItem(itemRegistry, "c", "gems/" + oreName);
        if (gem.isPresent()) {
            return BuiltInRegistries.ITEM.getKey(gem.get()).toString();
        }

        return null;
    }

    public static String extractOreColor(ResourceManager resourceManager, Item oreItem) {
        Block block = Block.byItem(oreItem);
        if (block == null || block == Blocks.AIR) {
            return "#808080";
        }

        Identifier blockId = BuiltInRegistries.BLOCK.getKey(block);
        Identifier textureLocation = Identifier.fromNamespaceAndPath(
                blockId.getNamespace(),
                "textures/block/" + blockId.getPath() + ".png"
        );

        try {
            var resource = resourceManager.getResource(textureLocation);
            if (resource.isEmpty()) return "#808080";

            Set<Integer> basePalette = loadBasePalette(resourceManager, blockId);

            try (InputStream stream = resource.get().open();
                 NativeImage image = NativeImage.read(stream)) {

                int width = image.getWidth();
                int height = Math.min(image.getHeight(), width);

                Map<Integer, double[]> buckets = new HashMap<>();

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pixel = image.getPixel(x, y);
                        int a = (pixel >> 24) & 0xFF;
                        if (a < 128) continue;

                        int r = (pixel >> 16) & 0xFF;
                        int g = (pixel >> 8) & 0xFF;
                        int b = pixel & 0xFF;

                        if (basePalette.contains(quantize(r, g, b))) continue;

                        float sat = saturation(r, g, b);
                        if (sat < 0.10f) continue;

                        int key = quantize(r, g, b);
                        double[] bucket = buckets.computeIfAbsent(key, k -> new double[5]);
                        bucket[0] += r * sat;
                        bucket[1] += g * sat;
                        bucket[2] += b * sat;
                        bucket[3] += sat;
                        bucket[4] += 1.0;
                    }
                }

                if (buckets.isEmpty()) return fallbackAverage(image);

                double[] best = null;
                double bestScore = -1;
                for (double[] bk : buckets.values()) {
                    double score = bk[3] * Math.sqrt(bk[4]);
                    if (score > bestScore) {
                        bestScore = score;
                        best = bk;
                    }
                }

                int rOut = (int) Math.round(best[0] / best[3]);
                int gOut = (int) Math.round(best[1] / best[3]);
                int bOut = (int) Math.round(best[2] / best[3]);
                return String.format("#%02X%02X%02X", clamp(rOut), clamp(gOut), clamp(bOut));
            }
        } catch (Exception e) {
            return "#808080";
        }
    }

    private static Set<Integer> loadBasePalette(ResourceManager rm, Identifier oreBlockId) {
        String path = oreBlockId.getPath();
        String basePath;
        if (path.startsWith("deepslate_") || path.contains("/deepslate_")) {
            basePath = "textures/block/deepslate.png";
        } else if (path.startsWith("nether_") || path.equals("ancient_debris") || path.contains("/nether_")) {
            basePath = "textures/block/netherrack.png";
        } else if (path.startsWith("end_stone_") || path.startsWith("end_") || path.contains("/end_stone_")) {
            basePath = "textures/block/end_stone.png";
        } else {
            basePath = "textures/block/stone.png";
        }

        Set<Integer> palette = new HashSet<>();
        try {
            var resource = rm.getResource(Identifier.fromNamespaceAndPath("minecraft", basePath));
            if (resource.isEmpty()) return palette;
            try (InputStream stream = resource.get().open();
                 NativeImage image = NativeImage.read(stream)) {
                int w = image.getWidth();
                int h = Math.min(image.getHeight(), w);
                for (int y = 0; y < h; y++) {
                    for (int x = 0; x < w; x++) {
                        int p = image.getPixel(x, y);
                        if (((p >> 24) & 0xFF) < 128) continue;
                        int r = (p >> 16) & 0xFF;
                        int g = (p >> 8) & 0xFF;
                        int b = p & 0xFF;
                        palette.add(quantize(r, g, b));
                    }
                }
            }
        } catch (Exception ignored) {}
        return palette;
    }

    private static int quantize(int r, int g, int b) {
        return ((r >> 3) << 10) | ((g >> 3) << 5) | (b >> 3);
    }

    private static float saturation(int r, int g, int b) {
        int max = Math.max(r, Math.max(g, b));
        if (max == 0) return 0;
        int min = Math.min(r, Math.min(g, b));
        return (max - min) / (float) max;
    }

    private static int clamp(int v) {
        return Math.max(0, Math.min(255, v));
    }

    private static String fallbackAverage(NativeImage image) {
        int w = image.getWidth();
        int h = Math.min(image.getHeight(), w);
        long tr = 0, tg = 0, tb = 0;
        int count = 0;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int p = image.getPixel(x, y);
                if (((p >> 24) & 0xFF) < 128) continue;
                tr += (p >> 16) & 0xFF;
                tg += (p >> 8) & 0xFF;
                tb += p & 0xFF;
                count++;
            }
        }
        if (count == 0) return "#808080";
        return String.format("#%02X%02X%02X", (int) (tr / count), (int) (tg / count), (int) (tb / count));
    }
}
