package com.breakinblocks.neovitae.datagen.book.spiritus;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class SentientSwordEntry extends EntryProvider {

    public SentientSwordEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Sentient Sword");
        this.pageText("The [#](8B0000)Sentient Sword[#]() is a far more elegant instrument of Will collection than the "
                + "[#](8B0000)Soul Snare[#]() could ever aspire to be. It may feel sluggish when first drawn -- the blade "
                + "feeds on the Will you carry, growing sharper with every fragment. A [#](8B0000)Spiritus Gem[#]() brimming "
                + "with Will is essential to unlocking its true potential.\\\n\\\n"
                + "[#](2E8B57)As with all Sentient equipment, the sword may be repaired with Crystallized Will in an Anvil.[#]()");

        this.page("recipe", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Forging the Blade");
        this.pageText("This weapon shall serve you well in the hunts to come.\\\n\\\n"
                + "[#](2E8B57)Right-click to recalibrate the blade's attunement. Do this after acquiring a large quantity "
                + "of Will, or when experimenting with a new Aspect for the first time.[#]()");
    }

    @Override
    protected String entryName() {
        return "Sentient Sword";
    }

    @Override
    protected String entryDescription() {
        return "A Will-devouring blade that reaps essence from the slain.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.SENTIENT_SWORD.get());
    }

    @Override
    protected String entryId() {
        return "sentient_sword";
    }
}
