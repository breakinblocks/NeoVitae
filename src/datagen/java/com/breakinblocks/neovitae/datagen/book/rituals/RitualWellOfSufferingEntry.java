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
import net.minecraft.resources.ResourceLocation;
import com.mojang.datafixers.util.Pair;

public class RitualWellOfSufferingEntry extends EntryProvider {

    public RitualWellOfSufferingEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "ritual/well_of_suffering"))
                .withMultiblockName("Well of Suffering")
                .withText(this.context().pageText()));
        this.pageText("Use a [#](8B0000)Ritual Diviner [Dusk][#]() for easier construction.");

        this.page("stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("well_of_suffering")));

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Ritual Info");
        this.pageText("This ritual attacks mobs within its damage zone and puts the harvested [#](8B0000)Life Essence[#]() into a nearby [#](8B0000)Ara Vitae[#](). Put a [#](8B0000)Blood Orb[#]() in the Altar, maybe add a few [#](8B0000)Runes of Sacrifice[#]() for good measure and you'll never have to worry about your LP supplies again... as long as you can supply enough mobs.");

        this.page("details", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The Ritual can only link to one altar at a time. Mobs will still drop non-Player Kill mob drops.");
    }

    @Override
    protected String entryName() {
        return "Well of Suffering";
    }

    @Override
    protected String entryDescription() {
        return "Damages mobs and fills a Ara Vitae with LP.";
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
        return "ritual_well_of_suffering";
    }
}
