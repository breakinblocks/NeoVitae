package com.breakinblocks.neovitae.datagen.book.altar;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;

public class SlatesEntry extends EntryProvider {

    public SlatesEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Slates");
        this.pageText("The [#](8B0000)Ara Vitae[#]() does more than fill orbs; it is a crucible for forging "
                + "[#](8B0000)Slates[#](), the inscribed stone tablets that serve as the foundation for nearly every "
                + "vitaemantic creation. Each successive tier of slate demands a more powerful altar and a "
                + "steeper offering of [#](4A0080)Essentia Vitae[#]().");

        this.page("blank_reinforced", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("[#](B8860B)Tabula Rasa[#](): Lay deepslate upon a [#](B8860B)Tier 0[#]() altar. The basin drinks "
                + "[#](8B0000)1,000 EV[#]() and etches the first sigils into the stone."
                + "\\\n\\\n[#](B8860B)Tabula Robur[#](): Feed a Tabula Rasa to a [#](B8860B)Tier 1[#]() altar. "
                + "Cost: [#](8B0000)2,000 EV[#](). The sigils deepen, the stone hardens with purpose.");

        this.page("imbued_demonic", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("[#](B8860B)Tabula Animata[#](): A Tabula Robur offered to a [#](B8860B)Tier 2[#]() altar. "
                + "Cost: [#](8B0000)5,000 EV[#](). The stone now pulses with a faint, living warmth."
                + "\\\n\\\n[#](B8860B)Tabula Spiritus[#](): A Tabula Animata consumed by a [#](B8860B)Tier 3[#]() altar. "
                + "Cost: [#](8B0000)15,000 EV[#](). Dark veins thread through the tablet like frozen lightning.");

        this.page("ethereal", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("[#](B8860B)Tabula Aetherea[#](): A Tabula Spiritus surrendered to a [#](B8860B)Tier 4[#]() altar. "
                + "Cost: [#](8B0000)30,000 EV[#](). The slate becomes almost translucent, hovering at the edge "
                + "between the material and the [#](4A0080)beyond[#]().");
    }

    @Override
    protected String entryName() {
        return "The Slates";
    }

    @Override
    protected String entryDescription() {
        return "Inscribed stone tablets forged in the Ara Vitae, each tier more potent than the last.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.TABULA_RASA.get());
    }

    @Override
    protected String entryId() {
        return "slates";
    }
}
