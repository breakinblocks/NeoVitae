package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class TimeArraysEntry extends EntryProvider {

    public TimeArraysEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Day/Night Arrays");
        this.pageText("Time-based arrays are straight-forward arrays that control the time of day. The items "
                + "will be consumed once the array starts changing the time.\\\n\\\n"
                + "The **Day** array will change the time of day to the next sunrise. "
                + "The **Night** array will change the time of day to the next sunset.");

        this.page("recipes", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("**New Dawn**: Created via an Alchemy Array. Sets the time to the next sunrise.\\\n\\\n"
                + "**True Twilight**: Created via an Alchemy Array. Sets the time to the next sunset.");
    }

    @Override
    protected String entryName() {
        return "Day/Night Arrays";
    }

    @Override
    protected String entryDescription() {
        return "Arrays that control the time of day.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.CLOCK);
    }

    @Override
    protected String entryId() {
        return "time_arrays";
    }
}
