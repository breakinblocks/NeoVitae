package com.breakinblocks.neovitae.compat.modonomicon.page;

import com.breakinblocks.neovitae.common.recipe.NVRecipes;
import com.breakinblocks.neovitae.common.recipe.meteor.MeteorRecipe;
import com.breakinblocks.neovitae.compat.viewer.MeteorOutputEstimator;
import com.klikli_dev.modonomicon.client.gui.book.button.SmallArrowButton;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import com.klikli_dev.modonomicon.client.render.page.BookPageRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookMeteorCatalogPageRenderer extends BookPageRenderer<BookMeteorCatalogPage> {

    private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("#,###");
    private static final int NAME_COLOR = 0x4A0080;
    private static final int TEXT_COLOR = 0x555555;
    private static final int COLUMNS = 6;
    private static final int SLOT_SIZE = 20;
    private static final int GRID_X = (BookEntryScreen.PAGE_WIDTH - (COLUMNS * SLOT_SIZE - 4)) / 2;
    private static final int NAV_X = BookEntryScreen.PAGE_WIDTH - 24;
    private static final int NAV_Y = 144;

    private final Map<ResourceLocation, List<MeteorOutputEstimator.Estimate>> estimates = new HashMap<>();
    private List<RecipeHolder<MeteorRecipe>> meteors = List.of();
    private int index;

    public BookMeteorCatalogPageRenderer(BookMeteorCatalogPage page) {
        super(page);
    }

    @Override
    public void onBeginDisplayPage(BookEntryScreen parentScreen, int left, int top) {
        super.onBeginDisplayPage(parentScreen, left, top);
        this.meteors = collectMeteors();
        this.estimates.clear();
        this.index = Math.min(this.index, Math.max(0, this.meteors.size() - 1));

        this.addButton(new SmallArrowButton(parentScreen, NAV_X, NAV_Y, true,
                () -> this.index > 0, this::handleButtonArrow));
        this.addButton(new SmallArrowButton(parentScreen, NAV_X + 10, NAV_Y, false,
                () -> this.index < this.meteors.size() - 1, this::handleButtonArrow));
    }

    private void handleButtonArrow(Button button) {
        if (((SmallArrowButton) button).left) {
            this.index = Math.max(0, this.index - 1);
        } else {
            this.index = Math.min(this.meteors.size() - 1, this.index + 1);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int y = 0;
        if (this.page.hasTitle()) {
            this.renderTitle(guiGraphics, this.page.getTitle(), false, BookEntryScreen.PAGE_WIDTH / 2, 0);
            y += 14;
        }

        if (this.meteors.isEmpty()) {
            guiGraphics.drawWordWrap(this.font, Component.translatable("book.neovitae.meteor_catalog.empty"),
                    0, y, BookEntryScreen.PAGE_WIDTH, TEXT_COLOR);
            return;
        }

        RecipeHolder<MeteorRecipe> holder = this.meteors.get(this.index);
        MeteorRecipe meteor = holder.value();

        ItemStack offering = cycle(List.of(meteor.getInput().getItems()));
        this.parentScreen.renderItemStack(guiGraphics, 0, y, mouseX, mouseY, offering);
        guiGraphics.drawString(this.font, Language.getInstance().getVisualOrder(
                this.font.substrByWidth(offering.getHoverName(), BookEntryScreen.PAGE_WIDTH - 20)), 20, y, NAME_COLOR, false);
        int diameter = MeteorOutputEstimator.maxRadius(meteor) * 2 + 1;
        guiGraphics.drawString(this.font, Component.translatable("book.neovitae.meteor_catalog.size", diameter),
                20, y + 10, TEXT_COLOR, false);
        y += 22;

        guiGraphics.drawString(this.font, Component.translatable("book.neovitae.meteor_catalog.cost",
                NUMBER_FORMAT.format(meteor.getSyphon())), 0, y, TEXT_COLOR, false);
        y += 12;

        guiGraphics.drawString(this.font, Component.translatable("book.neovitae.meteor_catalog.contents"),
                0, y, NAME_COLOR, false);
        y += 11;

        List<MeteorOutputEstimator.Estimate> contents =
                this.estimates.computeIfAbsent(holder.id(), id -> MeteorOutputEstimator.estimate(meteor));
        for (int i = 0; i < contents.size(); i++) {
            MeteorOutputEstimator.Estimate estimate = contents.get(i);
            int slotX = GRID_X + (i % COLUMNS) * SLOT_SIZE;
            int slotY = y + (i / COLUMNS) * SLOT_SIZE;
            ItemStack stack = cycle(estimate.stacks());
            guiGraphics.renderItem(stack, slotX, slotY);
            if (this.parentScreen.isMouseInRange(mouseX, mouseY, slotX, slotY, 16, 16)) {
                this.parentScreen.setTooltip(tooltipFor(stack, estimate));
            }
        }

        if (this.meteors.size() > 1) {
            int boxX = NAV_X - 2;
            int boxY = NAV_Y - 2;
            guiGraphics.fill(boxX, boxY, boxX + 20, boxY + 11, 0x44000000);
            guiGraphics.fill(boxX - 1, boxY - 1, boxX + 20, boxY + 11, 0x44000000);
            Component counter = Component.translatable("book.neovitae.meteor_catalog.counter",
                    this.index + 1, this.meteors.size());
            guiGraphics.drawString(this.font, counter, boxX - 4 - this.font.width(counter), NAV_Y, TEXT_COLOR, false);
        }
    }

    private ItemStack cycle(List<ItemStack> stacks) {
        if (stacks.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return stacks.get((this.parentScreen.getTicksInBook() / 20) % stacks.size());
    }

    private static List<Component> tooltipFor(ItemStack stack, MeteorOutputEstimator.Estimate estimate) {
        List<Component> lines = new ArrayList<>();
        lines.add(stack.getHoverName());
        lines.add(Component.translatable("book.neovitae.meteor_catalog.share",
                String.format("%.1f", estimate.percentage())).withStyle(ChatFormatting.GRAY));
        if (estimate.poolSize() > 1) {
            lines.add(Component.translatable("book.neovitae.meteor_catalog.random_pool",
                    estimate.poolSize()).withStyle(ChatFormatting.GRAY));
        }
        return lines;
    }

    private static List<RecipeHolder<MeteorRecipe>> collectMeteors() {
        var level = Minecraft.getInstance().level;
        if (level == null) {
            return List.of();
        }
        List<RecipeHolder<MeteorRecipe>> list =
                new ArrayList<>(level.getRecipeManager().getAllRecipesFor(NVRecipes.METEOR_TYPE.get()));
        list.sort(Comparator.comparingInt((RecipeHolder<MeteorRecipe> h) -> h.value().getSyphon())
                .thenComparingInt(h -> MeteorOutputEstimator.maxRadius(h.value()))
                .thenComparing(RecipeHolder::id));
        return list;
    }

    @Override
    public Style getClickedComponentStyleAt(double x, double y) {
        return null;
    }
}
