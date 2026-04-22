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
import com.breakinblocks.neovitae.common.material.MaterialDefinition;
import com.breakinblocks.neovitae.common.material.MaterialRegistry;
import com.mojang.blaze3d.platform.NativeImage;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SmeltingRecipe;

public final class OreDiscoveryHelper {

    private OreDiscoveryHelper() {}

    public static List<MaterialDefinition> discoverNewMaterials(ServerLevel level) {
        Registry<Item> itemRegistry = level.registryAccess().lookupOrThrow(Registries.ITEM);
        ResourceManager resourceManager = level.getServer().getResourceManager();

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

            try (InputStream stream = resource.get().open();
                 NativeImage image = NativeImage.read(stream)) {

                int width = image.getWidth();
                int height = Math.min(image.getHeight(), width);

                long totalR = 0, totalG = 0, totalB = 0;
                int count = 0;

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pixel = image.getPixel(x, y);
                        int a = (pixel >> 24) & 0xFF;
                        if (a < 128) continue;

                        int r = (pixel >> 16) & 0xFF;
                        int g = (pixel >> 8) & 0xFF;
                        int b = pixel & 0xFF;

                        if (isStonePixel(r, g, b)) continue;

                        totalR += r;
                        totalG += g;
                        totalB += b;
                        count++;
                    }
                }

                if (count == 0) return "#808080";

                int avgR = (int) (totalR / count);
                int avgG = (int) (totalG / count);
                int avgB = (int) (totalB / count);

                return String.format("#%02X%02X%02X", avgR, avgG, avgB);
            }
        } catch (Exception e) {
            return "#808080";
        }
    }

    private static boolean isStonePixel(int r, int g, int b) {
        int maxDiff = Math.max(Math.abs(r - g), Math.max(Math.abs(r - b), Math.abs(g - b)));
        int avg = (r + g + b) / 3;
        return maxDiff < 30 && avg > 100 && avg < 180;
    }
}
