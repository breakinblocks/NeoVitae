package com.breakinblocks.neovitae.datagen.book.utility;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.fluid.NVFluids;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;

public class EssentiaVitaeBucketEntry extends EntryProvider {

    public EssentiaVitaeBucketEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Collecting the Essence");
        this.pageText("[#](4A0080)Essentia Vitae[#]() is potent within the Ara Vitae, where it fuels crafting "
                + "and flows through your Orb into the [#](4A0080)Anima[#](). But what if you wish to "
                + "place it in the world - to fill a moat around your Incense Altar, perhaps?\\\n\\\n"
                + "Simply place an empty Bucket in the Ara Vitae while it holds at least 1,000 "
                + "[#](4A0080)Essentia Vitae[#](). The altar fills the vessel in moments. "
                + "One unit of EV equals one millibucket.");

        this.page("recipe", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("A Bucket of Liquid Life");
        this.pageText("Place a Bucket in the Ara Vitae with at least 1,000 EV to receive a "
                + "[#](8B0000)Bucket of Essentia Vitae[#]().\\\n\\\n"
                + "It is emphatically not blood. Blood would have coagulated long ago.\\\n\\\n"
                + "...Why are you looking at me like that, apprentice?");
    }

    @Override
    protected String entryName() {
        return "Bucket of Essentia Vitae";
    }

    @Override
    protected String entryDescription() {
        return "Drawing the refined essence from the altar in liquid form.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVFluids.ESSENTIA_VITAE_BUCKET.get());
    }

    @Override
    protected String entryId() {
        return "essentia_vitae_bucket";
    }
}
