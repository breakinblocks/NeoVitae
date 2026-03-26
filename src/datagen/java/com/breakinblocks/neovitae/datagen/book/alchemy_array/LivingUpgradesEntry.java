package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class LivingUpgradesEntry extends EntryProvider {

    public LivingUpgradesEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Living Upgrades");
        this.pageText("You have felt it -- the armour shifting, adapting, straining to assist you in whatever "
                + "task it observes. It grows in many directions at once, but its capacity is finite. "
                + "Trying to master everything yields mastery of nothing.");

        this.page("specialization", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Consider forging multiple sets, each trained for a specific purpose. A miner's set, "
                + "a warrior's set, an explorer's set -- specialization is the path to true power.\\\n\\\n"
                + "You have devised [#](8B0000)Rituals[#]() to assist with focused training, and another "
                + "to imbue your armour with a greater capacity for growth.");
    }

    @Override
    protected String entryName() {
        return "Living Upgrades";
    }

    @Override
    protected String entryDescription() {
        return "The armour learns from your deeds -- guide its growth with care.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.UPGRADE_TOME.get());
    }

    @Override
    protected String entryId() {
        return "living_upgrades";
    }
}
