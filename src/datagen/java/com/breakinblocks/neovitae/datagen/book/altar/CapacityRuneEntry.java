package com.breakinblocks.neovitae.datagen.book.altar;

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

public class CapacityRuneEntry extends EntryProvider {

    public CapacityRuneEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Rune of Capacity");
        this.pageText("The **Rune of Capacity** increases the capacity of the **Blood Altar** by an "
                + "additive +20%% per rune.");

        this.page("recipe", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "rune_capacity")));

        this.page("recipe2", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "rune_2_capacity"))
                .withText(this.context().pageText()));
        this.pageText("With some **Netherite Scrap** and some **Intricate Hellforged Parts** looted from the "
                + "**Demon Realm**, you can double the power of your **Rune of Capacity**, increasing the "
                + "capacity of the **Blood Altar** by an additive +40%% per rune.");

        this.page("reversion", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("If you change your mind, you can revert the upgraded rune back to its base version "
                + "in the Alchemical Reaction Chamber (ARC).");
    }

    @Override
    protected String entryName() {
        return "Rune of Capacity";
    }

    @Override
    protected String entryDescription() {
        return "Increases the Blood Altar's Life Essence capacity.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVBlocks.RUNE_CAPACITY.asItem());
    }

    @Override
    protected String entryId() {
        return "rune_capacity";
    }
}
