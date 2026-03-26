package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.breakinblocks.neovitae.datagen.book.page.BookLivingUpgradeTablePageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class ToughPalmsUpgradeEntry extends EntryProvider {

    public ToughPalmsUpgradeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookLivingUpgradeTablePageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Tough Palms");
        this.pageText("The armour toughens your hands against the bite of the sacrificial blade, yielding "
                + "more [#](4A0080)Essentia Vitae[#]() from each offering. Grants up to 150%% bonus to "
                + "self-sacrifice.\\\n\\\n"
                + "[#](B8860B)Trained by[#](): Offering blood with the [#](8B0000)Sacrificial Dagger[#]().\\\n\\\n"
                + "[#](B8860B)Maximum level[#](): 10");
    }

    @Override
    protected String entryName() {
        return "Tough Palms";
    }

    @Override
    protected String entryDescription() {
        return "Calloused hands yield more Essentia Vitae with each sacrifice.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.SACRIFICIAL_DAGGER.get());
    }

    @Override
    protected String entryId() {
        return "upgrade_tough_palms";
    }
}
