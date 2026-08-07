package com.breakinblocks.neovitae.compat.jei.meteor;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.apache.commons.lang3.tuple.Pair;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.meteor.MeteorLayer;
import com.breakinblocks.neovitae.common.meteor.RandomBlockContainer;
import com.breakinblocks.neovitae.common.meteor.RandomBlockTagContainer;
import com.breakinblocks.neovitae.common.recipe.meteor.MeteorRecipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import com.breakinblocks.neovitae.compat.jei.NVJeiRecipeIds;

/**
 * JEI recipe category for meteor ritual recipes.
 * Displays the catalyst input, all possible output blocks with weights, and recipe stats.
 */
public class MeteorRecipeCategory implements IRecipeCategory<MeteorRecipe> {

    public static final RecipeType<MeteorRecipe> RECIPE_TYPE = RecipeType.create(NeoVitae.MODID, "meteor", MeteorRecipe.class);
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,###");
    private static final int WIDTH = 170;
    private static final int HEIGHT = 120;

    private final IDrawable icon;

    public MeteorRecipeCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableItemStack(new ItemStack(NVBlocks.MASTER_RITUAL_STONE.block().get()));
    }

    @Override
    public RecipeType<MeteorRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Nonnull
    @Override
    public Component getTitle() {
        return Component.translatable("jei.neovitae.recipe.meteor");
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Nullable
    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void draw(MeteorRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Minecraft mc = Minecraft.getInstance();

        String costText = "Cost: " + DECIMAL_FORMAT.format(recipe.getSyphon()) + " EV";
        guiGraphics.drawString(mc.font, costText, 30, 3, Color.GRAY.getRGB(), false);

        String explosionText = "Explosion: " + recipe.getExplosionRadius();
        guiGraphics.drawString(mc.font, explosionText, 30, 13, Color.GRAY.getRGB(), false);

        int maxRadius = 0;
        for (MeteorLayer layer : recipe.getLayerList()) {
            maxRadius = Math.max(maxRadius, layer.getLayerRadius());
        }
        int diameter = maxRadius * 2 + 1;
        String sizeText = "Size: " + diameter + " Blocks";
        guiGraphics.drawString(mc.font, sizeText, 30, 23, Color.GRAY.getRGB(), false);

        // Draw "Catalyst:" label
        guiGraphics.drawString(mc.font, "Catalyst:", 0, 40, Color.DARK_GRAY.getRGB(), false);

        // Draw "Outputs:" label
        guiGraphics.drawString(mc.font, "Outputs:", 0, 58, Color.DARK_GRAY.getRGB(), false);
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, MeteorRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MeteorRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.CATALYST, 50, 38)
                .addIngredients(recipe.getInput());

        List<MeteorLayer> layers = recipe.getLayerList();
        int totalEstimatedBlocks = 0;
        Map<List<Block>, Double> blockEstimates = new LinkedHashMap<>();

        for (int i = 0; i < layers.size(); i++) {
            MeteorLayer layer = layers.get(i);
            int radius = layer.getLayerRadius();

            int innerRadius = (i > 0) ? layers.get(i - 1).getLayerRadius() : -1;
            int layerVolume = estimateSphereVolume(radius) - (innerRadius >= 0 ? estimateSphereVolume(innerRadius) : 0);
            totalEstimatedBlocks += layerVolume;

            int weightedTotal = 0;
            for (Pair<RandomBlockContainer, Integer> entry : layer.getWeightList()) {
                weightedTotal += entry.getValue();
            }
            int totalWeight = layer.getAdditionalTotalWeight() + weightedTotal;
            totalWeight = Math.max(layer.getMinWeight(), totalWeight);

            List<Block> fillBlocks = getBlocksFromContainer(layer.getFillBlock());
            if (!fillBlocks.isEmpty()) {
                double fillEstimate;
                if (totalWeight > 0) {
                    double fillPortion = (double) (totalWeight - weightedTotal) / totalWeight;
                    fillEstimate = layerVolume * fillPortion;
                } else {
                    fillEstimate = layerVolume;
                }
                blockEstimates.merge(fillBlocks, fillEstimate, Double::sum);
            }

            if (totalWeight > 0) {
                for (Pair<RandomBlockContainer, Integer> entry : layer.getWeightList()) {
                    List<Block> blocks = getBlocksFromContainer(entry.getKey());
                    if (!blocks.isEmpty()) {
                        int weight = entry.getValue();
                        double estimate = (double) weight / totalWeight * layerVolume;
                        blockEstimates.merge(blocks, estimate, Double::sum);
                    }
                }
            }

            if (layer.getShellBlock() != null) {
                List<Block> shellBlocks = getBlocksFromContainer(layer.getShellBlock());
                if (!shellBlocks.isEmpty()) {
                    int shellEstimate = estimateSphereVolume(radius) - estimateSphereVolume(radius - 1);
                    blockEstimates.merge(shellBlocks, (double) shellEstimate, Double::sum);
                }
            }
        }

        final int finalTotalBlocks = totalEstimatedBlocks;

        List<Map.Entry<List<Block>, Double>> sortedEntries = new ArrayList<>(blockEstimates.entrySet());
        sortedEntries.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        int slotIndex = 0;
        int slotsPerRow = 8;
        int startX = 5;
        int startY = 70;

        for (Map.Entry<List<Block>, Double> entry : sortedEntries) {
            if (slotIndex >= 24) break;

            List<ItemStack> stacks = new ArrayList<>();
            for (Block block : entry.getKey()) {
                ItemStack stack = new ItemStack(block);
                if (!stack.isEmpty()) {
                    stacks.add(stack);
                }
            }
            if (stacks.isEmpty()) continue;

            int row = slotIndex / slotsPerRow;
            int col = slotIndex % slotsPerRow;
            int x = startX + col * 18;
            int y = startY + row * 18;

            IRecipeSlotBuilder slot = builder.addSlot(RecipeIngredientRole.OUTPUT, x, y);
            slot.addItemStacks(stacks);
            int estimatedCount = (int) Math.round(entry.getValue());
            double percentage = finalTotalBlocks > 0 ? (double) estimatedCount / finalTotalBlocks * 100 : 0;
            int poolSize = stacks.size();

            slot.addRichTooltipCallback((view, tooltipBuilder) -> {
                tooltipBuilder.add(Component.translatable("jei.neovitae.recipe.meteor.estimate",
                        DECIMAL_FORMAT.format(estimatedCount), String.format("%.1f", percentage)));
                if (poolSize > 1) {
                    tooltipBuilder.add(Component.translatable("jei.neovitae.recipe.meteor.random_pool",
                            DECIMAL_FORMAT.format(poolSize)));
                }
            });
            slotIndex++;
        }
    }

    private int estimateSphereVolume(int radius) {
        if (radius < 0) return 0;
        int count = 0;
        float possOffset = 0.5f;
        float rSquared = (radius + possOffset) * (radius + possOffset);
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + y * y + z * z <= rSquared) {
                        count++;
                    }
                }
            }
        }
        return count;
    }


    private List<Block> getBlocksFromContainer(RandomBlockContainer container) {
        if (container == null) {
            return List.of();
        }

        if (container instanceof RandomBlockTagContainer tagContainer) {
            List<Block> blocks = new ArrayList<>();
            BuiltInRegistries.BLOCK.getTagOrEmpty(tagContainer.getTag()).forEach(holder -> blocks.add(holder.value()));

            int index = tagContainer.getIndex();
            if (index >= 0) {
                if (index >= blocks.size()) {
                    return List.of();
                }
                Block block = blocks.get(index);
                return block == Blocks.AIR ? List.of() : List.of(block);
            }

            blocks.removeIf(block -> block == Blocks.AIR);
            return blocks;
        }

        Block block = getBlockFromContainer(container);
        return block == null || block == Blocks.AIR ? List.of() : List.of(block);
    }

    @Nullable
    private Block getBlockFromContainer(RandomBlockContainer container) {
        if (container == null) {
            return null;
        }

        String entry = container.getEntry();
        if (entry.startsWith("#")) {
            String tagName = entry.substring(1);
            if (tagName.contains("#")) {
                tagName = tagName.substring(0, tagName.indexOf("#"));
            }
            try {
                ResourceLocation tagLoc = ResourceLocation.parse(tagName);
                var tagKey = TagKey.create(Registries.BLOCK, tagLoc);
                var optional = BuiltInRegistries.BLOCK.getTag(tagKey);
                if (optional.isPresent()) {
                    var holders = optional.get().stream().toList();
                    if (!holders.isEmpty()) {
                        return holders.get(0).value();
                    }
                }
            } catch (Exception e) {
                return null;
            }
        } else if (entry.startsWith(";")) {
            String fluidName = entry.substring(1);
            try {
                ResourceLocation fluidLoc = ResourceLocation.parse(fluidName);
                var fluid = BuiltInRegistries.FLUID.get(fluidLoc);
                if (fluid != null) {
                    return fluid.defaultFluidState().createLegacyBlock().getBlock();
                }
            } catch (Exception e) {
                return null;
            }
        } else {
            try {
                ResourceLocation blockLoc = ResourceLocation.parse(entry);
                return BuiltInRegistries.BLOCK.get(blockLoc);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public ResourceLocation getRegistryName(MeteorRecipe recipe) {
        return NVJeiRecipeIds.get(recipe);
    }
}
