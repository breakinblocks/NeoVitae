package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class TelepositionArrayEntry extends EntryProvider {

    public TelepositionArrayEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Teleposition Array");
        this.pageText("The [#](8B0000)Teleposition Array[#]() folds space between two aligned glyphs, "
                + "creating a seamless vertical transit system. A single array does nothing on its own "
                + "you must place [#](8B0000)at least two[#]() arrays directly above one another, aligned on "
                + "the same block column, at any distance up to [#](B8860B)64 blocks[#]()."
                + "\\\n\\\n[#](B8860B)Jump[#]() while standing on an array to teleport upward to the next array above. "
                + "[#](B8860B)Sneak[#]() to descend to the nearest array below.");

        this.page("details", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Multiple arrays can be chained vertically to create a multi-floor elevator shaft. "
                + "Each array searches only along the same X/Z column, so they must be precisely aligned."
                + "\\\n\\\n[#](2E8B57)Place one at the bottom of your base and one at the top for instant vertical transit. "
                + "Add intermediate floors as needed, each jump or sneak moves you to the nearest array, "
                + "not the furthest.[#]()");
    }

    @Override
    protected String entryName() {
        return "Teleposition Array";
    }

    @Override
    protected String entryDescription() {
        return "A vertical transit glyph that folds space between aligned arrays.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.ENDER_PEARL);
    }

    @Override
    protected String entryId() {
        return "teleposition_array";
    }
}
