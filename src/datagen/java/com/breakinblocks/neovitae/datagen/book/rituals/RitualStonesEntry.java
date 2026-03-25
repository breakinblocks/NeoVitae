package com.breakinblocks.neovitae.datagen.book.rituals;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import net.minecraft.resources.ResourceLocation;
import com.mojang.datafixers.util.Pair;

public class RitualStonesEntry extends EntryProvider {

    public RitualStonesEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Ritual Stones");
        this.pageText("**Ritual Stones** are the canvas upon which you will draw your **Rituals**. They also look quite nifty, and can be manually painted with the various **Elemental Inscription Tools**."
                + "\n\nNote that the Inscription Tools used to have durability, but now they last forever! Rejoice, you can decorate your base with **Fire Ritual Stones** to your heart's content.");

        this.page("recipes", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "ritual_stone_blank"))
                .withRecipeId2(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "ritual_stone_master")));
    }

    @Override
    protected String entryName() {
        return "Ritual Stones";
    }

    @Override
    protected String entryDescription() {
        return "The building blocks for all rituals.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVBlocks.WATER_RITUAL_STONE.asItem());
    }

    @Override
    protected String entryId() {
        return "ritual_stones";
    }
}
