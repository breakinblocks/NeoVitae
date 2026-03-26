package com.breakinblocks.neovitae.datagen.book.utility;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.fluid.NVFluids;
import com.mojang.datafixers.util.Pair;

public class LifeEssenceBucketEntry extends EntryProvider {

    public LifeEssenceBucketEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Bucket of Life");
        this.pageText("[#](8B0000)Life Essence[#]() is all fine and good in the Blood Altar, where it can be used for crafting "
                + "or funnelled into an Orb to power Rituals, but what if you want to build a moat of the stuff "
                + "around your Incense Altar? Fortunately, extracting Life Essence is relatively trivial. Simply "
                + "place a Bucket in the Blood Altar and wait a few seconds for it to fill up. 1 LP = 1mb, "
                + "so 1,000 LP should be plenty for your Bucket.");

        this.page("recipe", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Blood Altar Recipe");
        this.pageText("Place a Bucket in the Blood Altar with at least 1,000 LP to receive a Bucket of Life Essence.\\\n\\\n"
                + "*It's definitely not blood. Blood would have coagulated by now.\\\n\\\n"
                + "... Why are you looking at me like that?*");
    }

    @Override
    protected String entryName() {
        return "Bucket of Life";
    }

    @Override
    protected String entryDescription() {
        return "Extracting Life Essence from the Blood Altar.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVFluids.LIFE_ESSENCE_BUCKET.get());
    }

    @Override
    protected String entryId() {
        return "life_essence_bucket";
    }
}
