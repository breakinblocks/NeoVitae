package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class BloodLampSigilEntry extends EntryProvider {

    public BloodLampSigilEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Sigil of the Blood Lamp");
        this.pageText("The [#](8B0000)Sigil of the Blood Lamp[#]() launches a mote of crystallized "
                + "[#](4A0080)Essentia Vitae[#]() in the direction you face. When it strikes a surface, it "
                + "anchors itself as a near-invisible light source at a cost of merely 10 "
                + "[#](4A0080)Essentia Vitae[#]().\\\n\\\n"
                + "Indispensable for miners, dungeon-delvers, and any blood mage who refuses to suffer "
                + "darkness in their domain.");

        this.page("recipe", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Forge the [#](8B0000)Blood Lamp Reagent[#]() in the [#](8B0000)Tabula Vitae[#](), then inscribe "
                + "an [#](8B0000)Alchemy Array[#]() with the reagent as base and a slate as catalyst.\\\n\\\n"
                + "[#](4A0080)A small light to banish the deepest dark.[#]()");
    }

    @Override
    protected String entryName() {
        return "Sigil of the Blood Lamp";
    }

    @Override
    protected String entryDescription() {
        return "Cast motes of blood-light into the darkness ahead of you.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.SIGIL_BLOOD_LIGHT.get());
    }

    @Override
    protected String entryId() {
        return "sigil_blood_lamp";
    }
}
