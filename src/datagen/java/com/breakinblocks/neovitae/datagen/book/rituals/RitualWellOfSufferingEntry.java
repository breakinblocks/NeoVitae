package com.breakinblocks.neovitae.datagen.book.rituals;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookMultiblockPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.fluid.NVFluids;
import com.breakinblocks.neovitae.datagen.book.page.BookRitualInfoPageModel;
import net.minecraft.resources.Identifier;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;

public class RitualWellOfSufferingEntry extends EntryProvider {

    public RitualWellOfSufferingEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "ritual/well_of_suffering"))
                .withMultiblockName("Well of Suffering")
                .withText(this.context().pageText()));
        this.pageText("[#](2E8B57)Use a Ritual Diviner [Dusk] for easier construction.[#]()");

        this.page("stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("well_of_suffering")));

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Crimson Tithe");
        this.pageText("Perhaps the most infamous ritual in all of [#](4A0080)Vitaemancy[#](). The circle inflicts suffering upon every creature within its reach, siphoning [#](4A0080)Essentia Vitae[#]() from their agony and channeling it into a nearby [#](8B0000)Ara Vitae[#](). Place an [#](8B0000)Orb of Vitae[#]() in the altar and line its walls with [#](8B0000)Runes of Sacrifice[#](); provided you can supply enough victims, your reserves will never run dry.");

        this.page("details", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("[#](2E8B57)The ritual links to only one altar at a time. Slain creatures still yield their normal non-player-kill drops.[#]()");
    }

    @Override
    protected String entryName() {
        return "Well of Suffering";
    }

    @Override
    protected String entryDescription() {
        return "Harvests Essentia Vitae from the suffering of others.";
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
        return "ritual_well_of_suffering";
    }
}
