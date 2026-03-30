package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class RedstoneArraysEntry extends EntryProvider {

    public RedstoneArraysEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Redstone Arrays");
        this.pageText("These arrays bridge the gap between vitaemancy and redstone engineering.\\\n\\\n"
                + "The [#](8B0000)Signal Array[#]() outputs a redstone signal from 0 to 15, proportional to "
                + "the Essentia Vitae stored in the owner's soul network. It scales linearly up to "
                + "1,000,000 EV. Requires a bound [#](8B0000)Arcane Ash[#]() to function.");

        this.page("trigger", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The [#](8B0000)Trigger Array[#]() emits a brief redstone pulse whenever a mob or player "
                + "steps on it. The pulse lasts half a second, long enough for any standard redstone "
                + "mechanism to detect.\\\n\\\n"
                + "It functions much like a pressure plate, but is nearly invisible once inscribed.\\\n\\\n"
                + "[#](4A0080)The blood knows who walks above it.[#]()");
    }

    @Override
    protected String entryName() {
        return "Redstone Arrays";
    }

    @Override
    protected String entryDescription() {
        return "Arrays that interface with redstone mechanisms.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.REDSTONE);
    }

    @Override
    protected String entryId() {
        return "redstone_arrays";
    }
}
