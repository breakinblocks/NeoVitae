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

public class AccelerationRuneEntry extends EntryProvider {

    public AccelerationRuneEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Acceleration Rune");
        this.pageText("The **Acceleration Rune** increases the rate of a couple operations. While normally "
                + "the operations of the **Charging Rune** and **Displacement Rune** occur every 20 ticks, "
                + "one tick of the delay is removed per rune, down to a minimum of 1 operation per tick.");

        this.page("recipe", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "rune_acceleration")));

        this.page("recipe2", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "rune_2_acceleration"))
                .withText(this.context().pageText()));
        this.pageText("With some **Netherite Scrap** and some **Intricate Hellforged Parts** looted from the "
                + "**Demon Realm**, you can double the power of your **Acceleration Rune**, removing two ticks "
                + "per rune instead of one, freeing up other slots around your altar.");

        this.page("reversion", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("If you change your mind, you can revert the upgraded rune back to its base version "
                + "in the Alchemical Reaction Chamber (ARC).");
    }

    @Override
    protected String entryName() {
        return "Acceleration Rune";
    }

    @Override
    protected String entryDescription() {
        return "Reduces the tick delay for Charging and Displacement Rune operations.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVBlocks.RUNE_ACCELERATION.asItem());
    }

    @Override
    protected String entryId() {
        return "rune_acceleration";
    }
}
