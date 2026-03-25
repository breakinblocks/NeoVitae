package com.breakinblocks.neovitae.datagen.book.utility;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;

public class LavaCrystalEntry extends EntryProvider {

    public LavaCrystalEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Lava Crystal");
        this.pageText("The **Lava Crystal** is a source of great heat. Between the lava used in its creation "
                + "and the power of your Soul Network, you feel confident that it will never cool.\n\n"
                + "Pressing Use while looking at any block in the world will ignite it, at a cost of 100 LP.");

        this.page("recipe", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath("neovitae", "lava_crystal"))
                .withText(this.context().pageText()));
        this.pageText("Furthermore, if placed in the fuel slot of a Furnace, it will act as a never-ending "
                + "fuel source, consuming 50 LP to burn for 10 seconds, or long enough to cook one item.");
    }

    @Override
    protected String entryName() {
        return "Lava Crystal";
    }

    @Override
    protected String entryDescription() {
        return "An infinite fuel source powered by LP.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.LAVA_CRYSTAL.get());
    }

    @Override
    protected String entryId() {
        return "lava_crystal";
    }
}
