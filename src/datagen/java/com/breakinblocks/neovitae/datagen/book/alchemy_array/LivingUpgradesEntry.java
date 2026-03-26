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
        this.pageTitle("Living Equipment Upgrades");
        this.pageText("While wearing this new armour, you have felt it growing, trying to assist you with "
                + "various tasks it has seen you perform.\\\n\\\n"
                + "It seems to be able to perform in a number of areas, but its growth is limited, and trying "
                + "to do everything at once is quite fruitless.");

        this.page("specialization", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Perhaps multiple specialised sets may be a good idea? Of course, you'll have to train "
                + "it carefully if you want more than a smattering of poorly-directed benefits.\\\n\\\n"
                + "Fortunately, you have devised a [#](8B0000)Ritual[#]() that will assist with training, and another one "
                + "that will imbue your armour with a greater ability to grow.");
    }

    @Override
    protected String entryName() {
        return "Living Equipment Upgrades";
    }

    @Override
    protected String entryDescription() {
        return "Overview of the Living Equipment upgrade system.";
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
