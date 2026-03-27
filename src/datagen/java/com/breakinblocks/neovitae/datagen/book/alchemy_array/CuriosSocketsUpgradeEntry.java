package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.breakinblocks.neovitae.datagen.book.page.BookLivingUpgradeTablePageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class CuriosSocketsUpgradeEntry extends EntryProvider {

    public CuriosSocketsUpgradeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookLivingUpgradeTablePageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Socketed");
        this.pageText("The armour opens hidden recesses within itself, forming [#](8B0000)Living Armour "
                + "Socket[#]() slots that can hold compatible [#](4A0080)Vitaemancy[#]() trinkets.\\\n\\\n"
                + "This upgrade cannot be trained; only inscribed via [#](8B0000)Upgrade Tome[#](). "
                + "Each tome adds one additional socket.\\\n\\\n"
                + "[#](2E8B57)Requires the Curios API.[#]()");
    }

    @Override
    protected String entryName() {
        return "Socketed";
    }

    @Override
    protected String entryDescription() {
        return "The armour grows sockets to hold your vitaemantic trinkets.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.SPIRITUS_SNARE.get());
    }

    @Override
    protected String entryId() {
        return "upgrade_curios_sockets";
    }
}
