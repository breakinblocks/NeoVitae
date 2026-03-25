package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class MovementArraysEntry extends EntryProvider {

    public MovementArraysEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Movement Arrays");
        this.pageText("The Movement Arrays are a pair of arrays that throw players, mobs, items, etc in a "
                + "specific direction. One will throw them horizontally, while the other will throw them vertically.");

        this.page("recipes", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("**Speed Array**: Launches entities horizontally in the direction they are facing. "
                + "Created with an Alchemy Array.\n\n"
                + "**Updraft Array**: Launches entities vertically upward. "
                + "Created with an Alchemy Array.");
    }

    @Override
    protected String entryName() {
        return "Movement Arrays";
    }

    @Override
    protected String entryDescription() {
        return "Arrays that launch entities horizontally or vertically.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.FEATHER);
    }

    @Override
    protected String entryId() {
        return "movement_arrays";
    }
}
