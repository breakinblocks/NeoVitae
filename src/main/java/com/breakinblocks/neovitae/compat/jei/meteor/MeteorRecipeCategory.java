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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.apache.commons.lang3.tuple.Pair;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.BMBlocks;
import com.breakinblocks.neovitae.common.meteor.MeteorLayer;
import com.breakinblocks.neovitae.common.meteor.RandomBlockContainer;
import com.breakinblocks.neovitae.common.recipe.meteor.MeteorRecipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

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
        icon = guiHelper.createDrawableItemStack(new ItemStack(BMBlocks.MASTER_RITUAL_STONE.block().get()));
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

        // Draw header info
        String costText = "Cost: " + DECIMAL_FORMAT.format(recipe.getSyphon()) + " LP";
        guiGraphics.drawString(mc.font, costText, 30, 3, Color.GRAY.getRGB(), false);

        // Draw explosion radius
        String explosionText = "Explosion: " + recipe.getExplosionRadius();
        guiGraphics.drawString(mc.font, explosionText, 30, 13, Color.GRAY.getRGB(), false);

        // Calculate and display max radius
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
        // No additional tooltips needed beyond slot tooltips
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MeteorRecipe recipe, IFocusGroup focuses) {
        // Add catalyst input slot - this enables "U" key lookup on the catalyst item
        builder.addSlot(RecipeIngredientRole.CATALYST, 50, 38)
                .addIngredients(recipe.getInput());

        // Process layers - calculate per-block estimates
        List<MeteorLayer> layers = recipe.getLayerList();
        int totalEstimatedBlocks = 0;
        Map<Block, Double> blockEstimates = new LinkedHashMap<>();

        for (int i = 0; i < layers.size(); i++) {
            MeteorLayer layer = layers.get(i);
            int radius = layer.getLayerRadius();

            int innerRadius = (i > 0) ? layers.get(i - 1).getLayerRadius() : -1;
            int layerVolume = estimateSphereVolume(radius) - (innerRadius >= 0 ? estimateSphereVolume(innerRadius) : 0);
            totalEstimatedBlocks += layerVolume;

            // Calculate total weight for this layer
            int weightedTotal = 0;
            for (Pair<RandomBlockContainer, Integer> entry : layer.getWeightList()) {
                weightedTotal += entry.getValue();
            }
            int totalWeight = layer.getAdditionalTotalWeight() + weightedTotal;
            totalWeight = Math.max(layer.getMinWeight(), totalWeight);

            // Calculate fill block estimate for this layer
            Block fillBlock = getBlockFromContainer(layer.getFillBlock());
            if (fillBlock != null && fillBlock != Blocks.AIR) {
                double fillEstimate;
                if (totalWeight > 0) {
                    // Fill gets the remainder after weighted blocks
                    double fillPortion = (double) (totalWeight - weightedTotal) / totalWeight;
                    fillEstimate = layerVolume * fillPortion;
                } else {
                    // No weighted blocks, fill gets entire layer
                    fillEstimate = layerVolume;
                }
                blockEstimates.merge(fillBlock, fillEstimate, Double::sum);
            }

            // Calculate weighted block estimates for this layer
            if (totalWeight > 0) {
                for (Pair<RandomBlockContainer, Integer> entry : layer.getWeightList()) {
                    Block block = getBlockFromContainer(entry.getKey());
                    if (block != null && block != Blocks.AIR) {
                        int weight = entry.getValue();
                        double estimate = (double) weight / totalWeight * layerVolume;
                        blockEstimates.merge(block, estimate, Double::sum);
                    }
                }
            }

            // Shell blocks - rough estimate of surface area
            if (layer.getShellBlock() != null) {
                Block shellBlock = getBlockFromContainer(layer.getShellBlock());
                if (shellBlock != null && shellBlock != Blocks.AIR) {
                    int shellEstimate = estimateSphereVolume(radius) - estimateSphereVolume(radius - 1);
                    blockEstimates.merge(shellBlock, (double) shellEstimate, Double::sum);
                }
            }
        }

        final int finalTotalBlocks = totalEstimatedBlocks;

        // Sort blocks by estimated count (highest first)
        List<Map.Entry<Block, Double>> sortedEntries = new ArrayList<>(blockEstimates.entrySet());
        sortedEntries.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        int slotIndex = 0;
        int slotsPerRow = 8;
        int startX = 5;
        int startY = 70;

        for (Map.Entry<Block, Double> entry : sortedEntries) {
            if (slotIndex >= 24) break;

            int row = slotIndex / slotsPerRow;
            int col = slotIndex % slotsPerRow;
            int x = startX + col * 18;
            int y = startY + row * 18;

            IRecipeSlotBuilder slot = builder.addSlot(RecipeIngredientRole.OUTPUT, x, y);
            slot.addItemStack(new ItemStack(entry.getKey()));
            int estimatedCount = (int) Math.round(entry.getValue());
            double percentage = finalTotalBlocks > 0 ? (double) estimatedCount / finalTotalBlocks * 100 : 0;

            slot.addRichTooltipCallback((view, tooltipBuilder) ->
                tooltipBuilder.add(Component.translatable("jei.neovitae.recipe.meteor.estimate",
                        DECIMAL_FORMAT.format(estimatedCount), String.format("%.1f", percentage))));
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


    /**
     * Attempts to get a representative block from a RandomBlockContainer.
     * For tags, returns the first block in the tag, or null if empty.
     */
    @Nullable
    private Block getBlockFromContainer(RandomBlockContainer container) {
        if (container == null) {
            return null;
        }

        String entry = container.getEntry();
        if (entry.startsWith("#")) {
            // It's a tag - try to get first block
            String tagName = entry.substring(1);
            if (tagName.contains("#")) {
                tagName = tagName.substring(0, tagName.indexOf("#"));
            }
            try {
                ResourceLocation tagLoc = ResourceLocation.parse(tagName);
                // Return first block from tag
                var tagKey = net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.BLOCK, tagLoc);
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
            // It's a fluid - get the block form
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
            // Static block
            try {
                ResourceLocation blockLoc = ResourceLocation.parse(entry);
                return BuiltInRegistries.BLOCK.get(blockLoc);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}
