package com.breakinblocks.neovitae.datagen.book.demon_will;

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
        this.pageText("The **Sentient Sword** is a much more effective tool for collecting **Demon Will** than "
                + "**Soul Snares** could ever hope to be. It may seem weak at first, but it is powered by the "
                + "Wills you carry, so crafting a **Tartaric Gem** is a must.\\\n\\\n"
                + "Note that the sword, as with all **Sentient Tools**, can be repaired with **Crystallized "
                + "Will** in an Anvil.");

        this.page("recipe", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Sentient Sword");
        this.pageText("Craft the Sentient Sword in the Hellfire Forge.\\\n\\\n"
                + "This sword will serve you well.\\\n\\\n"
                + "Note that you can update the sword's stats by pressing right-click when you have it equipped "
                + "- if you have recently acquired a large amount of Will, or perhaps are trying out a new "
                + "Aspect of Will for the first time, then this may be a good idea.");
    }

    @Override
    protected String entryName() {
        return "Sentient Sword";
    }

    @Override
    protected String entryDescription() {
        return "A Demon Will-powered sword that harvests Will from slain enemies.";
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
