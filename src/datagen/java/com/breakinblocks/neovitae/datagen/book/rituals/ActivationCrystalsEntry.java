package com.breakinblocks.neovitae.datagen.book.rituals;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class ActivationCrystalsEntry extends EntryProvider {

    public ActivationCrystalsEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Activation Crystals");
        this.pageText("Your rituals require more than simply the correct arrangement of blocks and Sigils. An effort of will is required to open a channel from your Soul Network to the ritual, and the [#](8B0000)Activation Crystal[#]() will allow you to focus yourself enough to activate your rituals.");

        this.page("usage", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Weak Activation Crystal");
        this.pageText("The [#](8B0000)Weak Activation Crystal[#]() is crafted in the Blood Altar. Simply press Use with a bound Activation Crystal on a Master Ritual Stone to activate the ritual it's part of - assuming it's suitably assembled, that is.");
    }

    @Override
    protected String entryName() {
        return "Activation Crystals";
    }

    @Override
    protected String entryDescription() {
        return "Focus your will to activate rituals.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.ACTIVATION_CRYSTAL_WEAK.get());
    }

    @Override
    protected String entryId() {
        return "activation_crystals";
    }
}
