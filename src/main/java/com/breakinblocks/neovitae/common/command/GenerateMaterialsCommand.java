package com.breakinblocks.neovitae.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.Block;
import com.breakinblocks.neovitae.common.material.MaterialDefinition;
import com.breakinblocks.neovitae.common.material.MaterialRegistry;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class GenerateMaterialsCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("nvgenerate")
                        .requires(source -> source.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(GenerateMaterialsCommand::execute)
        );
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        source.sendSuccess(() -> Component.translatable("command.neovitae.generate.scanning"), false);

        Registry<Item> itemRegistry = level.registryAccess().registryOrThrow(Registries.ITEM);

        List<String> oreNames = itemRegistry.getTagNames()
                .filter(tag -> tag.location().getNamespace().equals("c"))
                .filter(tag -> tag.location().getPath().startsWith("ores/"))
                .map(tag -> tag.location().getPath().substring(5))
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        List<MaterialDefinition> newMaterials = new ArrayList<>();
        List<String> addedNames = new ArrayList<>();

        for (String oreName : oreNames) {
            if (MaterialRegistry.hasMaterial(oreName)) continue;

            TagKey<Item> oreTag = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "ores/" + oreName));
            Optional<Item> firstOre = itemRegistry.getTag(oreTag)
                    .flatMap(set -> set.stream().findFirst().map(Holder::value));

            if (firstOre.isEmpty()) continue;

            String smeltTo = findSmeltOutput(level, itemRegistry, firstOre.get(), oreName);
            float smeltXp = smeltTo != null ? 0.7f : 0;

            String rawTag = tagExists(itemRegistry, "c", "raw_materials/" + oreName) ? "c:raw_materials/" + oreName : null;
            String ingotTag = tagExists(itemRegistry, "c", "ingots/" + oreName) ? "c:ingots/" + oreName : null;
            if (ingotTag == null && tagExists(itemRegistry, "c", "gems/" + oreName)) {
                ingotTag = "c:gems/" + oreName;
            }

            String color = extractOreColor(level.getServer().getResourceManager(), firstOre.get(), oreName);

            List<String> stages = new ArrayList<>(List.of("fragment", "gravel", "dust"));

            MaterialDefinition def = new MaterialDefinition(
                    oreName, color, stages, smeltTo, smeltXp,
                    "c:ores/" + oreName, rawTag, ingotTag,
                    null, null
            );

            newMaterials.add(def);
            addedNames.add(oreName);
        }

        if (newMaterials.isEmpty()) {
            int existing = oreNames.size();
            source.sendSuccess(() -> Component.translatable("command.neovitae.generate.no_new", existing), false);
            return 0;
        }

        MaterialRegistry.appendAndSave(newMaterials);

        final String names = String.join(", ", addedNames);
        final int added = addedNames.size();
        final int skippedCount = oreNames.size() - added;
        source.sendSuccess(() -> Component.translatable("command.neovitae.generate.added", added, names), true);
        if (skippedCount > 0) {
            source.sendSuccess(() -> Component.translatable("command.neovitae.generate.skipped", skippedCount), false);
        }
        source.sendSuccess(() -> Component.translatable("command.neovitae.generate.restart").withStyle(net.minecraft.ChatFormatting.YELLOW), false);

        return added;
    }

    private static String findSmeltOutput(ServerLevel level, Registry<Item> itemRegistry, Item oreItem, String oreName) {
        var recipeManager = level.getRecipeManager();
        var input = new SingleRecipeInput(new ItemStack(oreItem));
        var recipe = recipeManager.getRecipeFor(RecipeType.SMELTING, input, level);
        if (recipe.isPresent()) {
            ItemStack result = recipe.get().value().getResultItem(level.registryAccess());
            if (!result.isEmpty()) {
                ResourceLocation id = BuiltInRegistries.ITEM.getKey(result.getItem());
                return id.toString();
            }
        }

        Optional<Item> ingot = getFirstFromTag(itemRegistry, "c", "ingots/" + oreName);
        if (ingot.isPresent()) {
            return BuiltInRegistries.ITEM.getKey(ingot.get()).toString();
        }

        Optional<Item> gem = getFirstFromTag(itemRegistry, "c", "gems/" + oreName);
        if (gem.isPresent()) {
            return BuiltInRegistries.ITEM.getKey(gem.get()).toString();
        }

        return null;
    }

    private static Optional<Item> getFirstFromTag(Registry<Item> registry, String namespace, String path) {
        TagKey<Item> tag = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(namespace, path));
        return registry.getTag(tag).flatMap(set -> set.stream().findFirst().map(Holder::value));
    }

    private static boolean tagExists(Registry<Item> registry, String namespace, String path) {
        TagKey<Item> tag = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(namespace, path));
        return registry.getTag(tag).map(set -> set.size() > 0).orElse(false);
    }

    public static String extractOreColorPublic(ResourceManager resourceManager, Item oreItem, String oreName) {
        return extractOreColor(resourceManager, oreItem, oreName);
    }

    private static String extractOreColor(ResourceManager resourceManager, Item oreItem, String oreName) {
        Block block = Block.byItem(oreItem);
        if (block == null || block == net.minecraft.world.level.block.Blocks.AIR) {
            return "#808080";
        }

        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);
        ResourceLocation textureLocation = ResourceLocation.fromNamespaceAndPath(
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
                        int pixel = image.getPixelRGBA(x, y);
                        int a = (pixel >> 24) & 0xFF;
                        if (a < 128) continue;

                        int r = pixel & 0xFF;
                        int g = (pixel >> 8) & 0xFF;
                        int b = (pixel >> 16) & 0xFF;

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
