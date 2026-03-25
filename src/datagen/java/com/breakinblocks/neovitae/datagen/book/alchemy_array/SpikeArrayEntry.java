package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class SpikeArrayEntry extends EntryProvider {

    public SpikeArrayEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Spike Array");
        this.pageText("The Spike Array is a rather simple array with a single purpose. When a living entity "
                + "steps into the array, they are hit with a full heart of damage. This is good for mob traps, "
                + "or for making your base a bit spikier for players.");

        this.page("recipe", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Create the **Spike Array** by drawing an Alchemy Array and applying the appropriate "
                + "base and catalyst items.");
    }

    @Override
    protected String entryName() {
        return "Spike Array";
    }

    @Override
    protected String entryDescription() {
        return "An array that damages entities that step on it.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.IRON_INGOT);
    }

    @Override
    protected String entryId() {
        return "spike_array";
    }
}
