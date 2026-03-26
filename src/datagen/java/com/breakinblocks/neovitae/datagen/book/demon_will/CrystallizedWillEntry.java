package com.breakinblocks.neovitae.datagen.book.demon_will;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class CrystallizedWillEntry extends EntryProvider {

    public CrystallizedWillEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Crystallized Will");
        this.pageText("Now that you have plenty of [#](8B0000)Demon Will[#]() in your [#](8B0000)Tartaric Gem[#](), it's time to explore "
                + "what happens when you unleash it upon the world.\\\n\\\n"
                + "First off, you'll need to get Will into the [#](8B0000)Aura[#](). Next, you'll need to make a "
                + "[#](8B0000)Demon Crystallizer[#]().");

        this.page("crystallizer", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Demon Crystallizer");
        this.pageText("Craft the Demon Crystallizer in the Hellfire Forge.\\\n\\\n"
                + "This will slowly consume [#](8B0000)Demon Will[#]() from the [#](8B0000)Aura[#]() to produce [#](8B0000)Will Crystals[#](). "
                + "The first spire costs 100 Will to form, and all subsequent spires cost 45 each, but can be "
                + "burned for 50 in the [#](8B0000)Vas Maleficum[#](), for a net gain of 5. The largest [#](8B0000)Crystal Cluster[#]() "
                + "can be up to 7 spires.");

        this.page("harvesting", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("If you have more than 512 total Will in your inventory (across any number of [#](8B0000)Tartaric "
                + "Gems[#]() and of any one type), you can harvest these crystals by right-clicking the spire with "
                + "an empty hand. This will remove all but the central spire.\\\n\\\n"
                + "However, if you do not have enough will, *really* need that central spire's Crystal, or are "
                + "just in a hurry, you can harvest the whole lot with a pickaxe.");

        this.page("related", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("5 will per harvested crystal isn't a particularly impressive haul, but rituals such as "
                + "[#](8B0000)Resonance of the Faceted Crystal[#](), [#](8B0000)Catalyst of the Forsaken Souls[#](), and [#](8B0000)Crack of the "
                + "Fractured Crystal[#](8B0000) (alongside [#]()Will Catalysts[#]()) should allow you to boost and automate "
                + "their production for some impressive gains.");
    }

    @Override
    protected String entryName() {
        return "Crystallized Will";
    }

    @Override
    protected String entryDescription() {
        return "Growing and harvesting Demon Will crystals.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.RAW_CRYSTAL.get());
    }

    @Override
    protected String entryId() {
        return "crystallized_will";
    }
}
