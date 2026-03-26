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
        this.pageText("Effect: Grants a bonus to Self Sacrifice, up to an additional 150%%.\\\n\\\n"
                + "Trained by: Sacrificing Blood with the Sacrificial Knife.\\\n\\\nMaximum level: 10");
    }

    @Override
    protected String entryName() {
        return "Tough Palms";
    }

    @Override
    protected String entryDescription() {
        return "Self sacrifice bonus from using the Sacrificial Knife.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.LAMINA_MALEFICUS.get());
    }

    @Override
    protected String entryId() {
        return "upgrade_tough_palms";
    }
}
